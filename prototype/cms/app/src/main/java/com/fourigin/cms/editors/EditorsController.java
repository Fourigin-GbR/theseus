package com.fourigin.cms.editors;

import com.fourigin.cms.InvalidParameterException;
import com.fourigin.cms.ServiceErrorResponse;
import com.fourigin.cms.models.ChecksumGenerator;
import com.fourigin.cms.models.content.ContentPageManager;
import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.UnresolvableContentPathException;
import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.repository.ContentRepository;
import com.fourigin.cms.repository.ContentRepositoryFactory;
import com.fourigin.cms.repository.UnresolvableSiteStructurePathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/editors")
public class EditorsController {

    private final Logger logger = LoggerFactory.getLogger(EditorsController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ContentElementResponse retrieve(@RequestBody RetrieveContentRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing retrieve request {}.", request);

        ContentElementResponse response = new ContentElementResponse();
        response.copyFrom(request);

        ContentElement contentElement = resolveContentElement(request, request.isFlushCaches());
        String currentChecksum = buildChecksum(contentElement);
        response.setCurrentContentElement(contentElement);
        response.setCurrentChecksum(currentChecksum);
        
        return response;
    }

    @RequestMapping(value = "/retrieveP", method = RequestMethod.GET)
    public ContentElementResponse r(
        @RequestParam("base") String base,
        @RequestParam("sitePath") String siteStructurePath,
        @RequestParam("contentPath") String contentPath,
        @RequestParam(value = "flush", required = false, defaultValue = "false") boolean flushCaches
    ){
        RetrieveContentRequest request = new RetrieveContentRequest();
        request.setBase(base);
        request.setSiteStructurePath(siteStructurePath);
        request.setContentPath(contentPath);
        request.setFlushCaches(flushCaches);

        return retrieve(request);
    }

    @RequestMapping(value = "/uptodate", method = RequestMethod.GET)
    public StatusAwareContentElementResponse isUpToDate(@RequestBody UpToDateRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing up-to-date request {}.", request);

        StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
        response.copyFrom(request);

        ContentElement contentElement = resolveContentElement(request, false);
        String currentChecksum = buildChecksum(contentElement);
        if(currentChecksum.equals(request.getChecksum())){
            response.setStatus(true);
            if (logger.isDebugEnabled()) logger.debug("Referenced content element is up-to-date.");
        }
        else {
            response.setStatus(false);
            response.setCurrentContentElement(contentElement);
            response.setCurrentChecksum(currentChecksum);
            if (logger.isDebugEnabled()) logger.debug("Referenced content element is not up-to-date. Current checksum is '{}'.", currentChecksum);
        }

        return response;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public StatusAwareContentElementResponse save(@RequestBody SaveChangesRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing save request {}.", request);

        StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
        response.copyFrom(request);

        ContentElement currentContentElement = resolveContentElement(request, false);
        String currentChecksum = buildChecksum(currentContentElement);
        response.setCurrentChecksum(currentChecksum);
        if(currentChecksum.equals(request.getOriginalChecksum())){
            ContentElement modifiedContentElement = request.getModifiedContentElement();
            updateContentElement(request, modifiedContentElement);
            response.setStatus(true);
            if (logger.isDebugEnabled()) logger.debug("Modified content element is updated.");
        }
        else {
            response.setStatus(false);
            response.setCurrentContentElement(currentContentElement);
            if (logger.isDebugEnabled()) logger.debug("Modified content element is not updated. Current checksum is '{}'.", currentChecksum);
        }

        return response;
    }

    @ExceptionHandler({
        UnresolvableContentPathException.class,
        UnresolvableSiteStructurePathException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse unresolvableContentPath(Exception ex) {
        if (logger.isErrorEnabled()) logger.error("Unable to resolve the content path!", ex);

        return new ServiceErrorResponse(404, "Unable to resolve the content path!", ex.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse invalidParameter(InvalidParameterException ex) {
        if (logger.isErrorEnabled()) logger.error("Invalid parameter!", ex);

        return new ServiceErrorResponse(500, "Unable to process request!", ex.getMessage());
    }

    private String buildChecksum(ContentElement contentElement){
        return ChecksumGenerator.getChecksum(contentElement);
    }

    private ContentElement resolveContentElement(ContentElementPointer pointer, boolean flushCaches){
        validate(pointer);

        String base = pointer.getBase();
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(base);
        if(contentRepository == null){
            throw new InvalidParameterException("No content repository available for '" + base + "'!");
        }

        if(flushCaches){
            if (logger.isDebugEnabled()) logger.debug("Flushing repository for '{}'.", base);
            contentRepository.flush();
        }

        String pagePath = pointer.getSiteStructurePath();
        PageInfo page = contentRepository.resolveInfo(PageInfo.class, pagePath);
        if(page == null){
            throw new InvalidParameterException("No page found for '" + pagePath + "'!");
        }

        PageInfo.ContentPageReference pageReference = page.getContentPageReference();
        String parentPath = pageReference.getParentPath();
        String contentId = pageReference.getContentId();
        ContentPage contentPage = contentRepository.retrieve(page);
        if(contentPage == null){
            throw new InvalidParameterException("No content found for path '" + parentPath + "' and id '" + contentId + "'!");
        }

        String contentPath = pointer.getContentPath();

        return ContentPageManager.resolve(contentPage, contentPath);
    }

    private void updateContentElement(ContentElementPointer pointer, ContentElement contentElement){
        validate(pointer);

        String base = pointer.getBase();
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(base);
        if(contentRepository == null){
            throw new InvalidParameterException("No content repository available for '" + base + "'!");
        }

        String pagePath = pointer.getSiteStructurePath();
        PageInfo page = contentRepository.resolveInfo(PageInfo.class, pagePath);
        if(page == null){
            throw new InvalidParameterException("No page found for '" + pagePath + "'!");
        }

        PageInfo.ContentPageReference pageReference = page.getContentPageReference();
        String parentPath = pageReference.getParentPath();
        String contentId = pageReference.getContentId();
        ContentPage contentPage = contentRepository.retrieve(page);
        if(contentPage == null){
            throw new InvalidParameterException("No content found for path '" + parentPath + "' and id '" + contentId + "'!");
        }

        String contentPath = pointer.getContentPath();
        ContentPageManager.update(contentPage, contentPath, contentElement);
    }

    private void validate(ContentElementPointer pointer){
        if(pointer == null){
            throw new IllegalArgumentException("Pointer must not be null!");
        }

        String pagePath = pointer.getSiteStructurePath();
        if(pagePath == null || pagePath.isEmpty()){
            throw new IllegalArgumentException("pointer's site structure path must not be null or empty!");
        }

        String contentPath = pointer.getContentPath();
        if(contentPath == null || contentPath.isEmpty()){
            throw new IllegalArgumentException("pointer's content path must not be null or empty!");
        }
    }

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }
}
