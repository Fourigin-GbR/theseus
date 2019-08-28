package com.fourigin.argo.controller.editors;

import com.fourigin.argo.InvalidParameterException;
import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.controller.ResponseStatus;
import com.fourigin.argo.controller.ServiceBeanRequest;
import com.fourigin.argo.controller.ServiceBeanResponse;
import com.fourigin.argo.controller.ServiceResponse;
import com.fourigin.argo.controller.editors.models.ContentContainer;
import com.fourigin.argo.controller.editors.models.ContentReference;
import com.fourigin.argo.controller.editors.models.SiteNode;
import com.fourigin.argo.controller.editors.models.SiteNodeContent;
import com.fourigin.argo.controller.editors.models.SiteStructure;
import com.fourigin.argo.controller.editors.models.SiteStructureElement;
import com.fourigin.argo.controller.editors.models.SiteStructureElementType;
import com.fourigin.argo.controller.editors.models.TemplateDescriptor;
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
import com.fourigin.argo.repository.TemplateResolver;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/{project}/editors")
public class EditorsController {

    private final Logger logger = LoggerFactory.getLogger(EditorsController.class);

    private CmsRequestAggregationResolver cmsRequestAggregationResolver;

    private ContentRepositoryFactory contentRepositoryFactory;

    private TemplateResolver templateResolver;

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

    @RequestMapping(value = "/prototype", method = RequestMethod.GET)
    public ContentPagePrototype retrievePrototype(
            @PathVariable String project,
            @RequestParam(RequestParameters.TEMPLATE_REF) String templateRef
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve prototype request for template {}", templateRef);

        Template template = templateResolver.retrieve(project, templateRef);

        return template.getPrototype();
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    public List<TemplateDescriptor> retrieveTemplates(
            @PathVariable String project
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve templates request");

        // TODO: implement me!
        throw new UnsupportedOperationException("No implemented! Needs to be defined with customer profiles");
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ServiceBeanResponse<ContentContainer> retrieve(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path,
            @RequestParam(RequestParameters.CONTENT_PATH) String contentPath
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing retrieve request for {}:{}:{}.", language, path, contentPath);

        CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                project,
                language,
                path
        );

        ContentPage contentPage = aggregation.getContentPage();
        ContentElement contentElement = ContentPageManager.resolve(contentPage, contentPath, false);
        String currentChecksum = buildChecksum(contentElement);

        ContentReference contentReference = new ContentReference();
        contentReference.setLanguage(language);
        contentReference.setPath(path);
        contentReference.setContentPath(contentPath);
        ContentContainer container = new ContentContainer();
        container.setContentReference(contentReference);
        container.setContentElement(contentElement);

        return new ServiceBeanResponse<>(container, currentChecksum, ResponseStatus.SUCCESS);
    }

    @RequestMapping(value = "/uptodate", method = RequestMethod.GET)
    public ServiceBeanResponse<ContentContainer> isUpToDate(
            @PathVariable String project,
            @RequestBody ServiceBeanRequest<ContentReference> request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing up-to-date request {}.", request);

        ContentReference contentReference = request.getPayload();

        MDC.put("project", project);
        MDC.put("locale", contentReference.getLanguage());

        try {
            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                    project,
                    contentReference.getLanguage(),
                    contentReference.getPath()
            );

            ContentElement contentElement = resolveContentElement(contentReference, aggregation);
            String currentChecksum = buildChecksum(contentElement);

            ContentContainer container = new ContentContainer();
            container.setContentReference(contentReference);
            container.setContentElement(contentElement);

            int status;
            if (currentChecksum.equals(request.getRevision())) {
                status = ResponseStatus.UNMODIFIED;
                if (logger.isDebugEnabled()) logger.debug("Referenced content element is up-to-date.");
            } else {
                status = ResponseStatus.FAILED_CONCURRENT_UPDATE;
                if (logger.isDebugEnabled())
                    logger.debug("Referenced content element is not up-to-date. Current checksum is '{}'.", currentChecksum);
            }

            return new ServiceBeanResponse<>(container, currentChecksum, status);
        } finally {
            MDC.remove("project");
            MDC.remove("locale");
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ServiceBeanResponse<ContentContainer> save(
            @PathVariable String project,
            @RequestBody ServiceBeanRequest<ContentContainer> request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing save request {}.", request);

        ContentContainer container = request.getPayload();
        ContentReference contentReference = container.getContentReference();

        MDC.put("project", project);
        MDC.put("locale", contentReference.getLanguage());

        try {
            CmsRequestAggregation aggregation = cmsRequestAggregationResolver.resolveAggregation(
                    project,
                    contentReference.getLanguage(),
                    contentReference.getPath()
            );

            ContentElement currentContentElement = resolveContentElement(contentReference, aggregation);
            String currentChecksum = buildChecksum(currentContentElement);

            int status;
            if (request.getRevision().equals(currentChecksum)) {
                status = ResponseStatus.SUCCESSFUL_UPDATE;
                ContentElement modifiedContentElement = container.getContentElement();

                updateContentElement(contentReference, modifiedContentElement, aggregation);
                if (logger.isDebugEnabled()) logger.debug("Modified content element has been updated.");
            } else {
                status = ResponseStatus.FAILED_CONCURRENT_UPDATE;
                container.setContentElement(currentContentElement);
                if (logger.isDebugEnabled())
                    logger.debug("Modified content element has been changed in the meantime. Current checksum is '{}'.", currentChecksum);
            }

            return new ServiceBeanResponse<>(container, currentChecksum, status);
        } finally {
            MDC.remove("project");
            MDC.remove("locale");
        }
    }

    @RequestMapping(value = "/siteStructure", method = RequestMethod.GET)
    public ServiceBeanResponse<SiteStructure> retrieveSiteStructure(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        List<SiteStructureElement> structure = new ArrayList<>();

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
        List<SiteNodeInfo> nodes = root.getNodes();
        processNodes(nodes, structure);

        SiteStructure siteStructure = new SiteStructure();
        siteStructure.setStructure(structure);
        return new ServiceBeanResponse<>(siteStructure, ChecksumGenerator.getChecksum(structure), ResponseStatus.SUCCESS);
    }

    @RequestMapping(value = "/siteStructure", method = RequestMethod.POST)
    public ServiceBeanResponse<SiteStructure> updateSiteStructure(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestBody ServiceBeanResponse<SiteStructure> request
    ) {
        List<SiteStructureElement> currentStructure = new ArrayList<>();

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
        List<SiteNodeInfo> nodes = root.getNodes();
        processNodes(nodes, currentStructure);

        String currentChecksum = ChecksumGenerator.getChecksum(currentStructure);

        SiteStructure siteStructure;
        int status;
        if(request.getRevision().equals(currentChecksum)) {
            status = ResponseStatus.SUCCESSFUL_UPDATE;

            siteStructure = request.getPayload();
            // TODO: update site structure!
            if (logger.isWarnEnabled()) logger.warn("Update site structure not implemented yet!");
        }
        else {
            status = ResponseStatus.FAILED_CONCURRENT_UPDATE;
            siteStructure = new SiteStructure();
            siteStructure.setStructure(currentStructure);
        }

        return new ServiceBeanResponse<>(siteStructure, currentChecksum, status);
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
    public ServiceBeanResponse<SiteNode> retrieveSiteNode(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path
    ) {
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);

        SiteNodeContent nodeContent = convert(info);

        SiteNode siteNode = new SiteNode();
        siteNode.setPath(path);
        siteNode.setContent(nodeContent);
        return new ServiceBeanResponse<>(siteNode, ChecksumGenerator.getChecksum(nodeContent), ResponseStatus.SUCCESS);
    }

    @RequestMapping(value = "/siteNode", method = RequestMethod.POST)
    public ServiceBeanResponse<SiteNode> updateSiteNode(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestBody ServiceBeanRequest<SiteNode> request
    ) {
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);

        SiteNode siteNode = request.getPayload();
        String path = siteNode.getPath();
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);
        SiteNodeContent nodeContent = siteNode.getContent();

        String currentChecksum = ChecksumGenerator.getChecksum(nodeContent);

        int status;
        if (request.getRevision().equals(currentChecksum)) {
            status = ResponseStatus.SUCCESSFUL_UPDATE;

            applyChanges(nodeContent, info);
            contentRepository.updateInfo(path, info);

            if (logger.isDebugEnabled())
                logger.debug("Modified site node has been updated.");
        }
        else {
            status = ResponseStatus.FAILED_CONCURRENT_UPDATE;

            SiteNodeContent currentContent = convert(info);
            siteNode.setContent(currentContent);

            if (logger.isDebugEnabled())
                logger.debug("Modified site node is not updated. Current checksum is '{}'.", currentChecksum);
        }

        return new ServiceBeanResponse<>(siteNode, currentChecksum, status);
    }

    @RequestMapping(value = "/siteNode", method = RequestMethod.PUT)
    public ServiceResponse addSiteNode(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestBody SiteNode siteNode
    ) {
        // TODO: implement me!
        return new ServiceResponse(ResponseStatus.SUCCESSFUL_CREATE);
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
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse unresolvableContentPath(Exception ex) {
        if (logger.isErrorEnabled()) logger.error("Unable to resolve the content path!", ex);

        return new ServiceErrorResponse(404, "Unable to resolve the content path!", ex.getMessage());
    }

    @ExceptionHandler({
            UnresolvableSiteStructurePathException.class
    })
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse unresolvableSitePath(Exception ex) {
        if (logger.isErrorEnabled()) logger.error("Unable to resolve the site path!", ex);

        return new ServiceErrorResponse(404, "Unable to resolve the site path!", ex.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceErrorResponse invalidParameter(InvalidParameterException ex) {
        if (logger.isErrorEnabled()) logger.error("Invalid parameter!", ex);

        return new ServiceErrorResponse(500, "Unable to process request!", ex.getMessage());
    }

    private String buildChecksum(ContentElement contentElement) {
        return ChecksumGenerator.getChecksum(contentElement);
    }

    private ContentElement resolveContentElement(ContentReference pointer, CmsRequestAggregation aggregation) {
        validate(pointer);

        String contentPath = pointer.getContentPath();
        ContentPage contentPage = aggregation.getContentPage();

        // resolve without detaching!
        return ContentPageManager.resolve(contentPage, contentPath, false);
    }

    private void updateContentElement(ContentReference pointer, ContentElement contentElement, CmsRequestAggregation aggregation) {
        validate(pointer);

        String contentPath = pointer.getContentPath();
        ContentPage contentPage = aggregation.getContentPage();

        ContentPageManager.update(contentPage, contentPath, contentElement);

        PageInfo pageInfo = aggregation.getPageInfo();
        ContentRepository contentRepository = aggregation.getContentRepository();
        contentRepository.update(pageInfo, contentPage);
    }

    private void validate(ContentReference pointer) {
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

    @Autowired
    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }
}
