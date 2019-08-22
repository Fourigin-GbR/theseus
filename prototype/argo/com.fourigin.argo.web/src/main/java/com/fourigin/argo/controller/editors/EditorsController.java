package com.fourigin.argo.controller.editors;

import com.fourigin.argo.InvalidParameterException;
import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.content.UnresolvableContentPathException;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.UnresolvableSiteStructurePathException;
import com.fourigin.argo.repository.aggregators.CmsRequestAggregation;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/{project}/editors")
public class EditorsController {

    private final Logger logger = LoggerFactory.getLogger(EditorsController.class);

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    private ContentRepositoryFactory contentRepositoryFactory;

    @RequestMapping(value = "/prototype", method = RequestMethod.GET)
    public ContentPagePrototype retrievePrototype(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve prototype request for language {} and sitePath {}", language, path);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(project, language, path);

        Template template = aggregation.getTemplate();

        return template.getPrototype();
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ContentElementResponse retrieve(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path,
            @RequestParam(RequestParameters.CONTENT_PATH) String contentPath
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve request for {}:{}:{}.", language, path, contentPath);

        ContentElementResponse response = new ContentElementResponse();
        response.setLanguage(language);
        response.setPath(path);
        response.setContentPath(contentPath);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                project,
                language,
                path
        );

        ContentElement contentElement = resolveContentElement(response, aggregation);
        String currentChecksum = buildChecksum(contentElement);
        response.setCurrentContentElement(contentElement);
        response.setCurrentChecksum(currentChecksum);

        return response;
    }

    @RequestMapping(value = "/uptodate", method = RequestMethod.GET)
    public StatusAwareContentElementResponse isUpToDate(
            @PathVariable String project,
            @RequestBody UpToDateRequest request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing up-to-date request {}.", request);

        MDC.put("project", project);
        MDC.put("locale", request.getLanguage());

        try {
            StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
            response.copyFrom(request);

            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                    project,
                    request.getLanguage(),
                    request.getPath()
            );

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
        } finally {
            MDC.remove("project");
            MDC.remove("locale");
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public StatusAwareContentElementResponse save(
            @PathVariable String project,
            @RequestBody SaveChangesRequest request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing save request {}.", request);

        MDC.put("project", project);
        MDC.put("locale", request.getLanguage());

        try {
            StatusAwareContentElementResponse response = new StatusAwareContentElementResponse();
            response.copyFrom(request);

            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                    project,
                    request.getLanguage(),
                    request.getPath()
            );

            ContentElement currentContentElement = resolveContentElement(request, aggregation);
            String currentChecksum = buildChecksum(currentContentElement);
            response.setCurrentChecksum(currentChecksum);
            if (currentChecksum.equals(request.getOriginalChecksum())) {
                response.setStatus(false);
                response.setCurrentContentElement(currentContentElement);
                if (logger.isDebugEnabled())
                    logger.debug("Modified content element is not updated. Current checksum is '{}'.", currentChecksum);
            } else {
                response.setStatus(true);
                ContentElement modifiedContentElement = request.getModifiedContentElement();
                updateContentElement(request, modifiedContentElement, aggregation);
                if (logger.isDebugEnabled()) logger.debug("Modified content element has been updated.");
            }

            return response;
        } finally {
            MDC.remove("project");
            MDC.remove("locale");
        }
    }

    @RequestMapping(value = "/siteStructure", method = RequestMethod.GET)
    public SiteStructure retrieveSiteStructure(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        List<SiteStructureElement> structure = new ArrayList<>();

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
        List<SiteNodeInfo> nodes = root.getNodes();
        processNodes(nodes, structure);

        SiteStructure result = new SiteStructure();
        result.setStructure(structure);
        result.setOriginalChecksum(ChecksumGenerator.getChecksum(structure));
        return result;
    }

    @RequestMapping(value = "/siteStructure", method = RequestMethod.POST)
    public UpdateSiteStructureResponse updateSiteStructure(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestBody SiteStructure siteStructure
    ) {
        UpdateSiteStructureResponse result = new UpdateSiteStructureResponse();

        List<SiteStructureElement> currentStructure = new ArrayList<>();

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
        List<SiteNodeInfo> nodes = root.getNodes();
        processNodes(nodes, currentStructure);

        String currentChecksum = ChecksumGenerator.getChecksum(currentStructure);

        if(!siteStructure.getOriginalChecksum().equals(currentChecksum)) {
            result.setStatus(false);
            siteStructure.setOriginalChecksum(currentChecksum);
            siteStructure.setStructure(currentStructure);
            result.setSiteStructure(siteStructure);
        }
        else {
            // TODO: update site structure!
            if (logger.isWarnEnabled()) logger.warn("Update site structure not implemented yet!");

            result.setStatus(true);
            result.setSiteStructure(siteStructure);
        }

        return result;
    }

    private void processNodes(List<SiteNodeInfo> nodes, List<SiteStructureElement> structure) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        for (SiteNodeInfo info : nodes) {
            SiteStructureElement element = new SiteStructureElement();

            // type
            if (DirectoryInfo.class.isAssignableFrom(info.getClass())) {
                element.setType(SiteStructureElementType.DIRECTORY);

                // process children
                DirectoryInfo directory = (DirectoryInfo) info;
                List<SiteStructureElement> subElements = new ArrayList<>();
                processNodes(directory.getNodes(), subElements);
                element.setChildren(subElements);
            } else if (PageInfo.class.isAssignableFrom(info.getClass())) {
                element.setType(SiteStructureElementType.PAGE);
            } else {
                element.setType(SiteStructureElementType.UNKNOWN);
            }

            // name
            element.setName(info.getName());

            structure.add(element);
        }
    }

    @RequestMapping(value = "/siteNode", method = RequestMethod.GET)
    public SiteNode retrieveSiteNode(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path
    ) {
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);

        SiteNodeContent nodeContent = convert(info);

        SiteNode result = new SiteNode();
        result.setPath(path);
        result.setContent(nodeContent);
        result.setOriginalChecksum(ChecksumGenerator.getChecksum(nodeContent));
        return result;
    }

    @RequestMapping(value = "/siteNode", method = RequestMethod.POST)
    public UpdateSiteNodeResponse updateSiteNode(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestBody SiteNode siteNode
    ) {
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);

        String path = siteNode.getPath();
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);

        SiteNodeContent nodeContent = siteNode.getContent();

        UpdateSiteNodeResponse result = new UpdateSiteNodeResponse();

        String currentChecksum = ChecksumGenerator.getChecksum(nodeContent);
        if (!siteNode.getOriginalChecksum().equals(currentChecksum)) {
            result.setStatus(false);
            SiteNodeContent currentContent = convert(info);
            siteNode.setContent(currentContent);
            siteNode.setOriginalChecksum(currentChecksum);
            result.setSiteNode(siteNode);

            if (logger.isDebugEnabled())
                logger.debug("Modified site element is not updated. Current checksum is '{}'.", currentChecksum);
        }
        else {
            applyChanges(nodeContent, info);

            contentRepository.updateInfo(path, info);

            result.setStatus(true);
            result.setSiteNode(siteNode);

            if (logger.isDebugEnabled())
                logger.debug("Modified content element has been updated.");
        }

        return result;
    }

    private SiteNodeContent convert(SiteNodeInfo info) {
        SiteNodeContent nodeContent = new SiteNodeContent();
        nodeContent.setName(info.getName());
        nodeContent.setDisplayName(info.getDisplayName());
        nodeContent.setLocalizedName(info.getLocalizedName());

        if (DirectoryInfo.class.isAssignableFrom(info.getClass())) {
            nodeContent.setType(SiteStructureElementType.DIRECTORY);
        } else if (PageInfo.class.isAssignableFrom(info.getClass())) {
            nodeContent.setType(SiteStructureElementType.PAGE);

            PageInfo page = (PageInfo) info;
            nodeContent.setTemplateReference(page.getTemplateReference());
        } else {
            nodeContent.setType(SiteStructureElementType.UNKNOWN);
        }

        return nodeContent;
    }

    private void applyChanges(SiteNodeContent nodeContent, SiteNodeInfo info) {
        switch (nodeContent.getType()) {
            case PAGE:
                ((PageInfo) info).setTemplateReference(nodeContent.getTemplateReference());
                break;
            case DIRECTORY:
                break;
            default:
                throw new UnsupportedOperationException("Unknown node type '" + nodeContent.getType() + "'!");
        }

        info.setName(nodeContent.getName());
        info.setDisplayName(nodeContent.getDisplayName());
        info.setLocalizedName(nodeContent.getLocalizedName());
    }

    @ExceptionHandler({
            UnresolvableContentPathException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse unresolvableContentPath(Exception ex) {
        if (logger.isErrorEnabled()) logger.error("Unable to resolve the content path!", ex);

        return new ServiceErrorResponse(404, "Unable to resolve the content path!", ex.getMessage());
    }

    @ExceptionHandler({
            UnresolvableSiteStructurePathException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse unresolvableSitePath(Exception ex) {
        if (logger.isErrorEnabled()) logger.error("Unable to resolve the site path!", ex);

        return new ServiceErrorResponse(404, "Unable to resolve the site path!", ex.getMessage());
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

        // resolve without detaching!
        return ContentPageManager.resolve(contentPage, contentPath, false);
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

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory factory) {
        this.contentRepositoryFactory = factory;
    }
}
