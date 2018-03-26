package com.fourigin.argo.compile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.compiler.PageCompiler;
import com.fourigin.argo.compiler.PageCompilerFactory;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.ContentResolver;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.fourigin.argo.template.engine.ProcessingMode.CMS;
import static com.fourigin.argo.template.engine.ProcessingMode.STAGE;

@Controller
@RequestMapping("/compile")
public class CompileController {

    private final Logger logger = LoggerFactory.getLogger(CompileController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    private PageCompilerFactory pageCompilerFactory;

    private CompilerOutputStrategy storageCompilerOutputStrategy;

    private ObjectMapper objectMapper;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<byte[]> compile(
        @RequestParam("base") String base,
        @RequestParam("path") String path,
        @RequestParam(value = "writeOutput", required = false, defaultValue = "false") boolean writeOutput,
        @RequestParam(value = "flush", required = false, defaultValue = "false") boolean flushCaches
    ){
        if (logger.isDebugEnabled()) logger.debug("Processing compile request for base {} & path {}.", base, path);

        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);
        if(flushCaches) {
            contentResolver.flush();
        }

        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(base);

        if(writeOutput){
            try {
                pageCompiler.compile(pageInfo, STAGE, storageCompilerOutputStrategy);
                storageCompilerOutputStrategy.finish();
            }
            catch(Throwable ex){
                storageCompilerOutputStrategy.reset();
            }
        }

        BufferedCompilerOutputStrategy bufferedCompilerOutputStrategy = new BufferedCompilerOutputStrategy();
        try {
            String outputContentType = pageCompiler.compile(pageInfo, CMS, bufferedCompilerOutputStrategy);
            byte[] bytes = bufferedCompilerOutputStrategy.getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(outputContentType));

            bufferedCompilerOutputStrategy.finish();

            return new HttpEntity<>(bytes, headers);
        } catch (Throwable ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling page!", ex);
            bufferedCompilerOutputStrategy.reset();
            throw ex;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/prepare-content", method = RequestMethod.GET)
    public ContentPage showPreparedContent(
        @RequestParam("base") String base,
        @RequestParam("path") String path,
        @RequestParam(value = "flush", required = false, defaultValue = "false") boolean flushCaches
    ){
        if (logger.isDebugEnabled()) logger.debug("Processing prepared-content request for base {} & path {}.", base, path);

        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);
        if(flushCaches) {
            contentResolver.flush();
        }

        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(base);

        ContentPage preparedContent = pageCompiler.prepareContent(pageInfo);

        if (logger.isDebugEnabled()) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                objectMapper.writeValue(baos, preparedContent);
                logger.debug("Prepared content:\n{}", new String(baos.toByteArray(), StandardCharsets.UTF_8));
            }
            catch(IOException ex){
                // whatever!
            }
        }

        return preparedContent;
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
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
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
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
