package com.fourigin.argo.editors;

import com.fourigin.argo.InvalidParameterException;
import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.compile.RequestParameters;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.content.UnresolvableContentPathException;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.UnresolvableSiteStructurePathException;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
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

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    @RequestMapping(value = "/prototype", method = RequestMethod.GET)
    public ContentPagePrototype retrievePrototype(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve prototype request for base {} and sitePath {}", base, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(base, path);

        Template template = aggregation.getTemplate();

        return template.getPrototype();
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ContentElementResponse retrieve(@RequestBody RetrieveContentRequest request) {
        if (logger.isDebugEnabled()) logger.debug("Processing retrieve request {}.", request);

        ContentElementResponse response = new ContentElementResponse();
        response.copyFrom(request);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(request.getBase(), request.getPath());

        ContentElement contentElement = resolveContentElement(request, aggregation);
        String currentChecksum = buildChecksum(contentElement);
        response.setCurrentContentElement(contentElement);
        response.setCurrentChecksum(currentChecksum);

        return response;
    }

    @RequestMapping(value = "/retrieveP", method = RequestMethod.GET)
    public ContentElementResponse r(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam(RequestParameters.PATH) String path,
        @RequestParam("contentPath") String contentPath
    ) {
        RetrieveContentRequest request = new RetrieveContentRequest();
        request.setBase(base);
        request.setPath(path);
        request.setContentPath(contentPath);

        return retrieve(request);
    }

    @RequestMapping(value = "/uptodate", method = RequestMethod.GET)
    public StatusAwareContentElementResponse isUpToDate(@RequestBody UpToDateRequest request) {
        if (logger.isDebugEnabled()) logger.debug("Processing up-to-date request {}.", request);

        StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
        response.copyFrom(request);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(request.getBase(), request.getPath());

        ContentElement contentElement = resolveContentElement(request, aggregation);
        String currentChecksum = buildChecksum(contentElement);
        if (currentChecksum.equals(request.getChecksum())) {
            response.setStatus(true);
            if (logger.isDebugEnabled()) logger.debug("Referenced content element is up-to-date.");
        } else {
            response.setStatus(false);
            response.setCurrentContentElement(contentElement);
            response.setCurrentChecksum(currentChecksum);
            if (logger.isDebugEnabled())
                logger.debug("Referenced content element is not up-to-date. Current checksum is '{}'.", currentChecksum);
        }

        return response;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public StatusAwareContentElementResponse save(@RequestBody SaveChangesRequest request) {
        if (logger.isDebugEnabled()) logger.debug("Processing save request {}.", request);

        StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
        response.copyFrom(request);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(request.getBase(), request.getPath());

        ContentElement currentContentElement = resolveContentElement(request, aggregation);
        String currentChecksum = buildChecksum(currentContentElement);
        response.setCurrentChecksum(currentChecksum);
        if (currentChecksum.equals(request.getOriginalChecksum())) {
            ContentElement modifiedContentElement = request.getModifiedContentElement();
            updateContentElement(request, modifiedContentElement, aggregation);
            response.setStatus(true);
            if (logger.isDebugEnabled()) logger.debug("Modified content element is updated.");
        } else {
            response.setStatus(false);
            response.setCurrentContentElement(currentContentElement);
            if (logger.isDebugEnabled())
                logger.debug("Modified content element is not updated. Current checksum is '{}'.", currentChecksum);
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

    private String buildChecksum(ContentElement contentElement) {
        return ChecksumGenerator.getChecksum(contentElement);
    }

    private ContentElement resolveContentElement(ContentElementPointer pointer, CmsRequestAggregation aggregation) {
        validate(pointer);

        String contentPath = pointer.getContentPath();
        ContentPage contentPage = aggregation.getContentPage();

        return ContentPageManager.resolve(contentPage, contentPath);
    }

    private void updateContentElement(ContentElementPointer pointer, ContentElement contentElement, CmsRequestAggregation aggregation) {
        validate(pointer);

        String contentPath = pointer.getContentPath();
        ContentPage contentPage = aggregation.getContentPage();

        ContentPageManager.update(contentPage, contentPath, contentElement);

        PageInfo pageInfo = aggregation.getPageInfo();
        ContentRepository contentRepository = aggregation.getContentRepository();
        contentRepository.update(pageInfo, contentPage);
    }

    private void validate(ContentElementPointer pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("Pointer must not be null!");
        }

        String pagePath = pointer.getPath();
        if (pagePath == null || pagePath.isEmpty()) {
            throw new IllegalArgumentException("pointer's site structure path must not be null or empty!");
        }

        String contentPath = pointer.getContentPath();
        if (contentPath == null || contentPath.isEmpty()) {
            throw new IllegalArgumentException("pointer's content path must not be null or empty!");
        }
    }

    @Autowired
    public void setCmsRequestAggregationResolver(CmsRequestAggregationResolver cmsRequestAggregationResolver) {
        this.cmsRequestAggregationResolver = cmsRequestAggregationResolver;
    }
}
