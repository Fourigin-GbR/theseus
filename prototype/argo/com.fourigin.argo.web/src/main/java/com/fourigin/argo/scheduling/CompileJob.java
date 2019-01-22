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

        long processingTimestamp = System.currentTimeMillis();

        int countCompiled = 0;
        int countSwitched = 0;
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

                        String path = (pageInfo.getPath() + '/' + pageInfo.getName()).replaceAll("//", "/");
                        MDC.put("path", path);

                        PageState pageState = contentRepository.resolvePageState(pageInfo);

                        // stage compile
                        if (compileStage(
                            path,
                            pageInfo,
                            pageState,
                            pageCompiler,
                            contentRepository,
                            processingTimestamp
                        )) {
                            countCompiled++;

                            contentRepository.updatePageState(pageInfo, pageState);
                        }

                        // live switch
                        if (switchLive(path, pageState, processingTimestamp)) {
                            countSwitched++;

                            contentRepository.updatePageState(pageInfo, pageState);
                        }
                    }
                }
            } finally {
                MDC.remove("customer");
                MDC.remove("base");
                MDC.remove("path");
            }
        }

        if (logger.isInfoEnabled()) logger.info("Compiled pages (in all bases): {}", countCompiled);
        if (logger.isInfoEnabled()) logger.info("Switched pages (in all bases): {}", countSwitched);
    }

    private boolean compileStage(String path, PageInfo pageInfo, PageState pageState, PageCompiler pageCompiler, ContentRepository contentRepository, long compileTimestamp) {
        if (!pageState.isStaged()) {
            if (logger.isDebugEnabled()) logger.debug("Skipping a non staged page");
            return false;
        }

        // calculate the current checksum
        ContentPage page = contentRepository.retrieve(pageInfo);
        pageState.buildChecksum(page);

        String pageName = pageInfo.getName();

        CompileState compileState = pageState.getCompileState();
        if (compileState != null) {
            if (logger.isDebugEnabled())
                logger.debug("Verifying compile state of the page '{}'.", pageName);

            String pageContentChecksum = pageState.getChecksum().getCombinedValue();
            String lastCompileChecksum = compileState.getChecksum();
            if (logger.isDebugEnabled())
                logger.debug("Page checksum: \n\tactual: {}\n\tlast:   {}", pageContentChecksum, lastCompileChecksum);

            if (pageContentChecksum.equals(lastCompileChecksum)) {
                if (logger.isDebugEnabled())
                    logger.debug("Skipping page '{}', checksum unchanged.", pageName);
                return false;
            }

            compileState.setChecksum(pageContentChecksum);
        } else {
            compileState = new CompileState();

            String pageContentChecksum = pageState.getChecksum().getCombinedValue();
            compileState.setChecksum(pageContentChecksum);
        }

        ProcessingMode mode = STAGE;
        ContentPage preparedContentPage = pageCompiler.prepareContent(pageInfo, mode);

        try {
            pageCompiler.compile(path, pageInfo, preparedContentPage, mode, storageCompilerOutputStrategy);
            storageCompilerOutputStrategy.finish();

            compileState.setCompiled(true);
            compileState.setTimestamp(compileTimestamp); // STAGE compile timestamp
        } catch (Throwable ex) {
            storageCompilerOutputStrategy.reset();

            compileState.setCompiled(false);
            compileState.setTimestamp(-1);
            compileState.setMessage(ex.getMessage());
        }

        pageState.setCompileState(compileState);

        return true;
    }

    private boolean switchLive(String path, PageState pageState, long switchTimestamp) {
        if (!pageState.isLive()) {
            if (logger.isDebugEnabled()) logger.debug("Skipping a non live-ready page '{}'", path);
            return false;
        }

        long timestampLiveSwitch = pageState.getTimestampLiveSwitch();
        if (timestampLiveSwitch > 0) {
            if (logger.isDebugEnabled()) logger.debug("Skipping an already live switched page '{}'", path);
            return false;
        }

        if (logger.isDebugEnabled()) logger.debug("Switching live page '{}' ...", path);
        // TODO: implement live switch!

        pageState.setTimestampLiveSwitch(switchTimestamp);
            
        return true;
    }
}
