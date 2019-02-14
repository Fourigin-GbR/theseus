package com.fourigin.argo.controller.compile;

import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.compiler.PageCompiler;
import com.fourigin.argo.compiler.PageCompilerFactory;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import com.fourigin.argo.strategies.BufferedCompilerOutputStrategy;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.ContentPageCompilerException;
import com.fourigin.argo.template.engine.ProcessingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static com.fourigin.argo.template.engine.ProcessingMode.CMS;

@Controller
@RequestMapping("/{customer}/compile")
public class CompileController {

    private final Logger logger = LoggerFactory.getLogger(CompileController.class);

    private PageCompilerFactory pageCompilerFactory;

    private CompilerOutputStrategy storageCompilerOutputStrategy;

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<byte[]> compile(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path
    ) {
        MDC.put("customer", customer);
        MDC.put("base", base);

        if (logger.isDebugEnabled()) logger.debug("Processing compile request for base '{}' & path '{}'.", base, path);



        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(customer, base, path);

        ContentRepository contentRepository = aggregation.getContentRepository();

        PageInfo pageInfo = aggregation.getPageInfo();
        PageState pageState = aggregation.getPageState();

        ContentPage page = aggregation.getContentPage();
        pageState.buildChecksum(page);

        CompileState compileState = pageState.getCompileState();
        if (compileState == null) {
            compileState = new CompileState();
        }

        String pageContentChecksum = pageState.getChecksum().getCombinedValue();
        if (logger.isDebugEnabled()) logger.debug("Actual page checksum: {}", pageContentChecksum);

        // TODO: move checksum check to method, that writes the output.
        // It doesn't make sense here to skip compiling, because e need a compiled content for the CMS!
        // *******
        /*

        String pageName = pageInfo.getName();
        if (compileState != null) {
            if (logger.isDebugEnabled()) logger.debug("Verifying compile state of the page '{}'.", pageName);

            String compileBaseChecksum = compileState.getChecksum();
            if (logger.isDebugEnabled()) logger.debug("Compile checksum: {}", compileBaseChecksum);

            if (compileBaseChecksum.equals(pageContentChecksum)) {
                if (logger.isInfoEnabled()) logger.info("Skipping page '{}', checksum unchanged.", pageName);
                return;
            }
            else {
                if (logger.isDebugEnabled()) logger.debug("");
            }
        } else {
            compileState = new CompileState();
        }
        */
        // *******

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(customer, base);

        ContentPage preparedContentPage = pageCompiler.prepareContent(pageInfo, CMS);

        BufferedCompilerOutputStrategy bufferedCompilerOutputStrategy = new BufferedCompilerOutputStrategy();
        try {
            String outputContentType = pageCompiler.compile(path, pageInfo, preparedContentPage, CMS, bufferedCompilerOutputStrategy);
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
            pageState.setStaged(false);

            throw ex;
        } finally {
//            long timestamp = System.currentTimeMillis();
//            compileState.setTimestamp(timestamp); // no compile timestamp for CMS mode!
            compileState.setChecksum(pageContentChecksum);

            String parentPath = pageInfo.getPath();
            contentRepository.updateInfo(parentPath, pageInfo);

            pageState.setCompileState(compileState);
            contentRepository.updatePageState(pageInfo, pageState);

            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    @ResponseBody
//    @RequestMapping(value = "/write-output", method = RequestMethod.POST)
    @RequestMapping(value = "/write-output", method = RequestMethod.GET) // TODO: temporary GET for batch processing
    public ResponseEntity<CompileResult> writeOutput(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path,
        @RequestParam(value = "mode", required = false, defaultValue = "STAGE") ProcessingMode mode,
        @RequestParam(value = "recursive", required = false, defaultValue = "false") boolean recursive
    ) {
        MDC.put("customer", customer);
        MDC.put("base", base);
        try {

            long startTimestamp = System.currentTimeMillis();
            if (!recursive) {
                try {
                    writePageOutput(customer, base, path, mode);
                } catch (Throwable ex) {
                    return new ResponseEntity<>(
                        new CompileResult(mode, false).withAttribute("cause", ex),
                        HttpStatus.BAD_REQUEST
                    );
                }
            } else {
                CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(customer, base, path);
                PageInfo info = aggregation.getPageInfo();

                DefaultPageInfoTraversingStrategy traversingStrategy = new DefaultPageInfoTraversingStrategy();
                Collection<PageInfo> nodes = traversingStrategy.collect(info.getParent());
                for (PageInfo node : nodes) {
                    String nodePath = node.getPath() + '/' + node.getName();
                    try {
                        writePageOutput(customer, base, nodePath, mode);
                    } catch (Throwable ex) {
                        return new ResponseEntity<>(
                            new CompileResult(mode, false).withAttribute("cause", ex),
                            HttpStatus.BAD_REQUEST
                        );
                    }
                }
            }

            long duration = System.currentTimeMillis() - startTimestamp;
            return new ResponseEntity<>(
                new CompileResult(mode, true).withAttribute("duration", duration),
                HttpStatus.OK
            );


//            if (logger.isDebugEnabled())
//                logger.debug("Processing write-output request for base '{}' & path '{}' with processing-mode {}.", base, path, mode);
//
//            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(customer, base, path);
//
//            PageInfo pageInfo = aggregation.getPageInfo();
//            PageState pageState = aggregation.getPageState();
//
//            CompileState compileState = pageState.getCompileState();
//            if (compileState == null) {
//                compileState = new CompileState();
//            }
//
//            if (!compileState.isCompiled()) {
//                return new ResponseEntity<>(
//                    new CompileResult(mode, false, "Page must have a successful compiled-state!"),
//                    HttpStatus.BAD_REQUEST
//                );
//            }
//
//            PageCompiler pageCompiler = pageCompilerFactory.getInstance(customer, base);
//
//            ContentPage preparedContentPage = pageCompiler.prepareContent(pageInfo, mode);
//
//            long startTimestamp = System.currentTimeMillis();
//            try {
//                pageCompiler.compile(path, pageInfo, preparedContentPage, mode, storageCompilerOutputStrategy);
//                storageCompilerOutputStrategy.finish();
//            } catch (Throwable ex) {
//                storageCompilerOutputStrategy.reset();
//                return new ResponseEntity<>(
//                    new CompileResult(mode, false).withAttribute("cause", ex),
//                    HttpStatus.BAD_REQUEST
//                );
//            }
//
//            long duration = System.currentTimeMillis() - startTimestamp;
//
//            return new ResponseEntity<>(
//                new CompileResult(mode, true).withAttribute("duration", duration),
//                HttpStatus.ok
//            );
        } finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    private void writePageOutput(String customer, String base, String path, ProcessingMode mode) {
        if (logger.isDebugEnabled())
            logger.debug("Processing write-output request for base '{}' & path '{}' with processing-mode {}.", base, path, mode);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(customer, base, path);

        PageInfo pageInfo = aggregation.getPageInfo();
//        PageState pageState = aggregation.getPageState();
//
//        CompileState compileState = pageState.getCompileState();
//        if (compileState == null) {
//            compileState = new CompileState();
//        }
//
//        if (!compileState.isCompiled()) {
//            return new ResponseEntity<>(
//                new CompileResult(mode, false, "Page must have a successful compiled-state!"),
//                HttpStatus.BAD_REQUEST
//            );
//        }

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(customer, base);

        ContentPage preparedContentPage = pageCompiler.prepareContent(pageInfo, mode);

        try {
            pageCompiler.compile(path, pageInfo, preparedContentPage, mode, storageCompilerOutputStrategy);
            storageCompilerOutputStrategy.finish();
        } catch (Throwable ex) {
            storageCompilerOutputStrategy.reset();
//            return new ResponseEntity<>(
//                new CompileResult(mode, false).withAttribute("cause", ex),
//                HttpStatus.BAD_REQUEST
//            );
        }

    }

    @ResponseBody
    @RequestMapping(value = "/prepare-content", method = RequestMethod.GET)
    public ContentPage showPreparedContent(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path
    ) {
        MDC.put("customer", customer);
        MDC.put("base", base);

        try {
            if (logger.isDebugEnabled())
                logger.debug("Processing prepared-content request for base {} & path {}.", base, path);

            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(customer, base, path);

            PageCompiler pageCompiler = pageCompilerFactory.getInstance(customer, base);

            return pageCompiler.prepareContent(aggregation.getPageInfo(), CMS);
        } finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

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
