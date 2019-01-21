package com.fourigin.argo.scheduling;


import com.fourigin.argo.compiler.PageCompiler;
import com.fourigin.argo.compiler.PageCompilerFactory;
import com.fourigin.argo.forms.config.CustomerSpecificConfiguration;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.ProcessingMode;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.fourigin.argo.template.engine.ProcessingMode.STAGE;

@Component
public class CompileJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(CompileJob.class);

    private static final boolean COMPILE_PAGES = true;

    @Autowired
    private PageCompilerFactory pageCompilerFactory;

    @Autowired
    private ContentRepositoryFactory contentRepositoryFactory;

    @Autowired
    @Qualifier("STAGE")
    private CompilerOutputStrategy storageCompilerOutputStrategy;

    @Autowired
    private CustomerSpecificConfiguration customerSpecificConfiguration;

    @Override
    public void execute(JobExecutionContext context) {
        if (logger.isInfoEnabled()) logger.info("Executing job ...");

        Map<String, Set<String>> allBases = customerSpecificConfiguration.getBases();

        for (Map.Entry<String, Set<String>> entry : allBases.entrySet()) {
            String customer = entry.getKey();
            Set<String> bases = entry.getValue();

            try {
                MDC.put("customer", customer);

                if (logger.isDebugEnabled()) logger.debug("Processing customer '{}'", customer);

                for (String base : bases) {
                    if (logger.isDebugEnabled()) logger.debug("Processing base '{}'", base);

                    MDC.put("base", base);

                    PageCompiler pageCompiler = pageCompilerFactory.getInstance(customer, base);
                    ContentRepository contentRepository = contentRepositoryFactory.getInstance(customer, base);

                    Collection<SiteNodeInfo> infoNodes = contentRepository.resolveNodeInfos("/", new DefaultPageInfoTraversingStrategy());
                    for (SiteNodeInfo infoNode : infoNodes) {
                        if (!PageInfo.class.isAssignableFrom(infoNode.getClass())) {
                            continue;
                        }

                        PageInfo pageInfo = PageInfo.class.cast(infoNode);
                        PageState pageState = contentRepository.resolvePageState(pageInfo);
                        CompileState compileState = pageState.getCompileState();

                        // calculate the current checksum
                        ContentPage page = contentRepository.retrieve(pageInfo);
                        pageState.buildChecksum(page);

                        String path = (pageInfo.getPath() + '/' + pageInfo.getName()).replaceAll("//", "/");
                        MDC.put("path", path);

                        if (!pageState.isStaged()) {
                            if (logger.isDebugEnabled()) logger.debug("Skipping a non staged page");
                            continue;
                        }

                        String pageContentChecksum = pageState.getChecksum().getCombinedValue();
                        if (logger.isDebugEnabled()) logger.debug("Actual page checksum: {}", pageContentChecksum);

                        String pageName = pageInfo.getName();
                        if (compileState != null) {
                            if (logger.isDebugEnabled())
                                logger.debug("Verifying compile state of the page '{}'.", pageName);

                            String compileBaseChecksum = compileState.getChecksum();
                            if (logger.isDebugEnabled()) logger.debug("Compile checksum: {}", compileBaseChecksum);

                            if (compileBaseChecksum.equals(pageContentChecksum)) {
                                if (logger.isInfoEnabled())
                                    logger.info("Skipping page '{}', checksum unchanged.", pageName);
                                continue;
                            }
                        }

                        if (COMPILE_PAGES) {
                            ProcessingMode mode = STAGE;
                            ContentPage preparedContentPage = pageCompiler.prepareContent(pageInfo, mode);

                            try {
                                pageCompiler.compile(path, pageInfo, preparedContentPage, mode, storageCompilerOutputStrategy);
                                storageCompilerOutputStrategy.finish();
                            } catch (Throwable ex) {
                                storageCompilerOutputStrategy.reset();
                            }
                        } else {
                            if (logger.isWarnEnabled()) logger.warn("Compiling of pages is currently disabled!");
                        }
                    }
                }
            } finally {
                MDC.remove("customer");
                MDC.remove("base");
                MDC.remove("path");
            }
        }
    }
}
