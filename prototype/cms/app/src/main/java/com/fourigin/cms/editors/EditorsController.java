package com.fourigin.cms.editors;

import com.fourigin.cms.models.ChecksumGenerator;
import com.fourigin.cms.models.content.ContentPageManager;
import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.repository.ContentPageRepository;
import com.fourigin.cms.repository.SiteStructureResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/editors")
public class EditorsController {

    private final Logger logger = LoggerFactory.getLogger(EditorsController.class);

    @Autowired
    private SiteStructureResolver siteStructureResolver;

    @Autowired
    private ContentPageRepository contentPageRepository;

    @RequestMapping(name = "/retrieve", method = RequestMethod.GET)
    @ResponseBody
    public ContentElementResponse retrieve(@RequestBody RetrieveContentRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing retrieve request {}.", request);

        ContentElementResponse response = new ContentElementResponse();
        response.copyFrom(request);

        ContentElement contentElement = resolveContentElement(request);
        String currentChecksum = buildChecksum(contentElement);
        response.setStatus(true);
        response.setCurrentContentElement(contentElement);
        response.setCurrentChecksum(currentChecksum);
        
        return response;
    }

    @RequestMapping(name = "/uptodate", method = RequestMethod.GET)
    @ResponseBody
    public ContentElementResponse isUpToDate(@RequestBody UpToDateRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing up-to-date request {}.", request);

        ContentElementResponse response = new ContentElementResponse();
        response.copyFrom(request);

        ContentElement contentElement = resolveContentElement(request);
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

    @RequestMapping(name = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ContentElementResponse save(@RequestBody SaveChangesRequest request){
        if (logger.isDebugEnabled()) logger.debug("Processing save request {}.", request);

        ContentElementResponse response = new ContentElementResponse();
        response.copyFrom(request);

        ContentElement currentContentElement = resolveContentElement(request);
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

    private String buildChecksum(ContentElement contentElement){
        return ChecksumGenerator.getChecksum(contentElement);
    }

    private ContentElement resolveContentElement(ContentElementPointer pointer){
        validate(pointer);

        String pagePath = pointer.getSiteStructurePath();
        PageInfo page = siteStructureResolver.resolveNode(PageInfo.class, pagePath);
        if(page == null){
            throw new IllegalArgumentException("No sitePage found for '" + pagePath + "'!");
        }

        PageInfo.ContentPageReference pageReference = page.getContentPageReference();
        String parentPath = pageReference.getParentPath();
        String contentId = pageReference.getContentId();
        ContentPage contentPage = contentPageRepository.retrieve(parentPath, contentId);
        if(contentPage == null){
            throw new IllegalArgumentException("No contentPage found for path '" + parentPath + "' and id '" + contentId + "'!");
        }

        String contentPath = pointer.getContentPath();

        return ContentPageManager.resolve(contentPage, contentPath);
    }

    private void updateContentElement(ContentElementPointer pointer, ContentElement contentElement){
        validate(pointer);

        String pagePath = pointer.getSiteStructurePath();
        PageInfo page = siteStructureResolver.resolveNode(PageInfo.class, pagePath);
        if(page == null){
            throw new IllegalArgumentException("No sitePage found for '" + pagePath + "'!");
        }

        PageInfo.ContentPageReference pageReference = page.getContentPageReference();
        String parentPath = pageReference.getParentPath();
        String contentId = pageReference.getContentId();
        ContentPage contentPage = contentPageRepository.retrieve(parentPath, contentId);
        if(contentPage == null){
            throw new IllegalArgumentException("No contentPage found for path '" + parentPath + "' and id '" + contentId + "'!");
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
}
