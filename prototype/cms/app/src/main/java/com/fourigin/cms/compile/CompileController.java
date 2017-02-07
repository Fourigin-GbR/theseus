package com.fourigin.cms.compile;

import com.fourigin.cms.ServiceErrorResponse;
import com.fourigin.cms.compiler.PageCompiler;
import com.fourigin.cms.compiler.PageCompilerFactory;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.repository.ContentRepositoryFactory;
import com.fourigin.cms.repository.ContentResolver;
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

@Controller
@RequestMapping("/compile")
public class CompileController {

    private final Logger logger = LoggerFactory.getLogger(CompileController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    private PageCompilerFactory pageCompilerFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<byte[]> compile(
        @RequestParam("base") String base,
        @RequestParam("path") String path
    ){
        if (logger.isDebugEnabled()) logger.debug("Processing compile request for base {} & path {}.", base, path);

        ContentResolver contentResolver = contentRepositoryFactory.getInstance(base);
        PageInfo pageInfo = contentResolver.resolveInfo(PageInfo.class, path);

        PageCompiler pageCompiler = pageCompilerFactory.getInstance(base);

        String outputContentType;
        byte[] bytes;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            outputContentType = pageCompiler.compile(pageInfo, out);
            bytes = out.toByteArray();
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling page!", ex);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(outputContentType));

        return new HttpEntity<>(bytes, headers);
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

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Autowired
    public void setPageCompilerFactory(PageCompilerFactory pageCompilerFactory) {
        this.pageCompilerFactory = pageCompilerFactory;
    }
}
