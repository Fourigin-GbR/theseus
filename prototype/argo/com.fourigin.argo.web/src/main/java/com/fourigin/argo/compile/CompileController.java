package com.fourigin.argo.compile;

import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.compiler.PageCompiler;
import com.fourigin.argo.compiler.PageCompilerFactory;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import com.fourigin.argo.strategies.BufferedCompilerOutputStrategy;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.ContentPageCompilerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.fourigin.argo.template.engine.ProcessingMode.CMS;
import static com.fourigin.argo.template.engine.ProcessingMode.STAGE;

@Controller
@RequestMapping("/compile")
public class CompileController {

    private final Logger logger = LoggerFactory.getLogger(CompileController.class);

    private PageCompilerFactory pageCompilerFactory;

    private CompilerOutputStrategy storageCompilerOutputStrategy;

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<byte[]> compile(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path,
        @RequestParam(value = "writeOutput", required = false, defaultValue = "false") boolean writeOutput
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing compile request for base {} & path {}.", base, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(base, path);

        PageInfo pageInfo = aggregation.getPageInfo();
        String pageName = pageInfo.getName();

        String pageContentChecksum = pageInfo.getChecksum().getCombinedValue();
        CompileState compileState = pageInfo.getCompileState();
        if(compileState != null){
            if (logger.isDebugEnabled()) logger.debug("Verifying compile state of the page '{}'.", pageName);
            String compileBaseChecksum = compileState.getChecksum();
            if(compileBaseChecksum.equals(pageContentChecksum)){
                if (logger.isInfoEnabled()) logger.info("Would skip compiling page, please implement me ..."); // NOPMD
                //                if (logger.isInfoEnabled()) logger.info("Skipping page '{}', checksum unchanged.", pageName);
                //                return;
            }
        }
        else {
            compileState = new CompileState();
        }

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(base);

        if (writeOutput) {
            try {
                pageCompiler.compile(path, pageInfo, STAGE, storageCompilerOutputStrategy);
                storageCompilerOutputStrategy.finish();
            } catch (Throwable ex) {
                storageCompilerOutputStrategy.reset();
            }
        }

        BufferedCompilerOutputStrategy bufferedCompilerOutputStrategy = new BufferedCompilerOutputStrategy();
        try {
            String outputContentType = pageCompiler.compile(path, pageInfo, CMS, bufferedCompilerOutputStrategy);
            byte[] bytes = bufferedCompilerOutputStrategy.getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(outputContentType));

            bufferedCompilerOutputStrategy.finish();

            compileState.setCompiled(true);

            return new HttpEntity<>(bytes, headers);
        } catch (Throwable ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling page!", ex);
            bufferedCompilerOutputStrategy.reset();

            compileState.setCompiled(false);

            throw ex;
        }
        finally {
            long timestamp = System.currentTimeMillis();
            compileState.setTimestamp(timestamp);
            compileState.setChecksum(pageContentChecksum);

            SiteNodeContainerInfo parent = pageInfo.getParent();
            String parentPath = parent.getPath();
            aggregation.getContentRepository().updateInfo(parentPath, pageInfo);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/prepare-content", method = RequestMethod.GET)
    public ContentPage showPreparedContent(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing prepared-content request for base {} & path {}.", base, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(base, path);

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(base);

        return pageCompiler.prepareContent(aggregation.getPageInfo());
    }

//    @RequestMapping(value = "/persist", method = RequestMethod.GET)
//    public StatusAwareContentElementResponse save(@RequestBody SaveChangesRequest request){
//
//    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceErrorResponse errorState(IllegalStateException ex) {
        if (logger.isErrorEnabled()) logger.error("Error processing request!", ex);

        return new ServiceErrorResponse(500, "Error processing request!", ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceErrorResponse errorState(IllegalArgumentException ex) {
        if (logger.isErrorEnabled()) logger.error("Error processing request!", ex);

        return new ServiceErrorResponse(500, "Error processing request!", ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(ContentPageCompilerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceErrorResponse errorState(ContentPageCompilerException ex) {
        if (logger.isErrorEnabled()) logger.error("Error while compiling content page!", ex);

        return new ServiceErrorResponse(500, "Error while compiling content page!", ex.getMessage(), ex.getCause());
    }

    @Autowired
    public void setPageCompilerFactory(PageCompilerFactory pageCompilerFactory) {
        this.pageCompilerFactory = pageCompilerFactory;
    }

    @Autowired
    public void setStorageCompilerOutputStrategy(CompilerOutputStrategy storageCompilerOutputStrategy) {
        this.storageCompilerOutputStrategy = storageCompilerOutputStrategy;
    }

    @Autowired
    public void setCmsRequestAggregationResolver(CmsRequestAggregationResolver cmsRequestAggregationResolver) {
        this.cmsRequestAggregationResolver = cmsRequestAggregationResolver;
    }
}
