package com.fourigin.argo.controller.editors;

import com.fourigin.argo.InvalidParameterException;
import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.controller.ServiceBeanResponse;
import com.fourigin.argo.controller.editors.models.ApplyActionStatus;
import com.fourigin.argo.controller.editors.models.SiteNode;
import com.fourigin.argo.controller.editors.models.SiteNodeContent;
import com.fourigin.argo.controller.editors.models.SiteStructure;
import com.fourigin.argo.controller.editors.models.SiteStructureElement;
import com.fourigin.argo.controller.editors.models.SiteStructureElementType;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.action.Action;
import com.fourigin.argo.models.action.ActionType;
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.UnresolvableSiteStructurePathException;
import com.fourigin.argo.repository.action.ActionRepository;
import com.fourigin.argo.repository.action.ActionRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fourigin.argo.controller.ServiceResponseStatus.SUCCESS;

@RestController
@RequestMapping("/{project}/site-structure")
public class SiteStructureEditorController {

    private final Logger logger = LoggerFactory.getLogger(SiteStructureEditorController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    private ActionRepositoryFactory actionRepositoryFactory;

    public SiteStructureEditorController(
            ContentRepositoryFactory contentRepositoryFactory,
            ActionRepositoryFactory actionRepositoryFactory
    ) {
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.actionRepositoryFactory = actionRepositoryFactory;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView overview(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing overview request for language {}.", language);

        // create result
        ModelAndView modelAndView = new ModelAndView("site-structure");

        modelAndView.addObject("project", project);
        modelAndView.addObject("language", language);

        return modelAndView;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView editor(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing editor request for language {}.", language);

        // create result
        ModelAndView modelAndView = new ModelAndView("site-structure-editor");

        modelAndView.addObject("project", project);
        modelAndView.addObject("language", language);

        return modelAndView;
    }

    /**
     * Retrieves Site Structure Model (SSM).
     * @param project current project
     * @param language current language
     * @return actual Site Structure Model with its current revision (checksum)
     */
    @RequestMapping(value = "/ssm", method = RequestMethod.GET)
    public ServiceBeanResponse<SiteStructure> retrieveSSM(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        if (logger.isDebugEnabled()) logger.debug("Retrieve SSM ('{}', '{}')", project, language);

        List<SiteStructureElement> structure = new ArrayList<>();

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
        List<SiteNodeInfo> nodes = root.getNodes();
        processNodes(nodes, structure);

        SiteStructure siteStructure = new SiteStructure();
        siteStructure.setStructure(structure);
        return new ServiceBeanResponse<>(siteStructure, ChecksumGenerator.getChecksum(structure), SUCCESS);
    }

//    @RequestMapping(value = "/siteStructure", method = RequestMethod.POST)
//    public ServiceBeanResponse<SiteStructure> updateSiteStructure(
//            @PathVariable String project,
//            @RequestParam(RequestParameters.LANGUAGE) String language,
//            @RequestBody ServiceBeanResponse<SiteStructure> request
//    ) {
//        List<SiteStructureElement> currentStructure = new ArrayList<>();
//
//        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
//        DirectoryInfo root = contentRepository.resolveInfo(DirectoryInfo.class, "/");
//        List<SiteNodeInfo> nodes = root.getNodes();
//        processNodes(nodes, currentStructure);
//
//        String currentChecksum = ChecksumGenerator.getChecksum(currentStructure);
//
//        SiteStructure siteStructure;
//        ServiceResponseStatus status;
//        if (request.getRevision().equals(currentChecksum)) {
//            status = SUCCESSFUL_UPDATE;
//
//            siteStructure = request.getPayload();
//            // TODO: update site structure!
//            if (logger.isWarnEnabled()) logger.warn("Update site structure not implemented yet!");
//        } else {
//            status = FAILED_CONCURRENT_UPDATE;
//            siteStructure = new SiteStructure();
//            siteStructure.setStructure(currentStructure);
//        }
//
//        return new ServiceBeanResponse<>(siteStructure, currentChecksum, status);
//    }

    /**
     * Retrieves Site Structure Item (SSI).
     * @param project current project
     * @param language current language
     * @param path site structure path
     * @return referenced Site Structure Item with its revision (checksum)
     */
    @RequestMapping(value = "/ssi", method = RequestMethod.GET)
    public ServiceBeanResponse<SiteNode> retrieveSSI(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.SITE_PATH) String path
    ) {
        if (logger.isDebugEnabled()) logger.debug("Retrieve SSI ('{}', '{}', '{}')", project, language, path);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);

        SiteNodeContent nodeContent = convert(info);

        SiteNode siteNode = new SiteNode();
        siteNode.setPath(path);
        siteNode.setContent(nodeContent);
        return new ServiceBeanResponse<>(siteNode, ChecksumGenerator.getChecksum(nodeContent), SUCCESS);
    }

    /**
     * Applies user action(s).
     * @param project current project code
     * @param actions user actions to apply
     * @param ssmRevision current SSM-Revision
     * @return status of applied actions
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public ServiceBeanResponse<Map<String, ApplyActionStatus>> applyActions(
            @PathVariable String project,
            @RequestParam("ssm-revision") String ssmRevision,
            @RequestBody List<Action> actions
    ) {
        if (logger.isDebugEnabled()) logger.debug("Applying action(s) for SSM-Revision '{}': {}", ssmRevision, actions);

        ActionRepository actionRepository = actionRepositoryFactory.getInstance(project);

        String newSsmRevision = null;
        Map<String, ApplyActionStatus> status = new HashMap<>();
        if (actions != null && !actions.isEmpty()) {
            for (Action action : actions) {
                // Apply the action
                String actionId = action.getId();
                long actionTimestamp = action.getTimestamp();
                ActionType actionType = action.getActionType();
                if (logger.isInfoEnabled()) logger.info("Applying action {} (timestamp: {}) of type {}",
                        actionId, actionTimestamp, actionType);

                switch (actionType) {
                    case CREATE:
//                        ActionCreate actionCreate = (ActionCreate) action;
//                        String targetFolder = actionCreate.getFolderPath();
//                        String insertionPath = actionCreate.getInsertionPath();

                        break;
                    case UPDATE:
                        break;
                    case DELETE:
                        break;
                    case MOVE:
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported action type '" + actionType + "'!");
                }

                newSsmRevision = ""; // TODO: calculate current revision after applying the action
                actionRepository.addAction(newSsmRevision, action);
                status.put(action.getId(), ApplyActionStatus.success());
            }
        }

        return new ServiceBeanResponse<>(status, newSsmRevision, SUCCESS);
    }

    /**
     * Retrieves an actions diff for specified SSM revisions.
     * @param project current project code
     * @param fromSsmRevision start SSM-Revision
     * @param toSsmRevision end SSM-Revision
     * @return the actions diff
     */
    @RequestMapping(value = "/diff", method = RequestMethod.GET)
    public List<Action> diffActions(
            @PathVariable String project,
            @RequestParam("from-ssm-revision") String fromSsmRevision,
            @RequestParam("to-ssm-revision") String toSsmRevision
    ) {
        if (logger.isDebugEnabled()) logger.debug("Resolving DIFF between revisions '{}' and '{}'", fromSsmRevision, toSsmRevision);

        ActionRepository actionRepository = actionRepositoryFactory.getInstance(project);
        List<Action> diff = actionRepository.resolveDiff(fromSsmRevision, toSsmRevision);
        if (logger.isDebugEnabled()) logger.debug("diff: {}", diff);

        return diff;
    }

//    @RequestMapping(value = "/siteNode", method = RequestMethod.POST)
//    public ServiceBeanResponse<SiteNode> updateSiteNode(
//            @PathVariable String project,
//            @RequestParam(RequestParameters.LANGUAGE) String language,
//            @RequestBody ServiceBeanRequest<SiteNode> request
//    ) {
//        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
//
//        SiteNode siteNode = request.getPayload();
//        String path = siteNode.getPath();
//        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);
//        SiteNodeContent nodeContent = siteNode.getContent();
//
//        String currentChecksum = ChecksumGenerator.getChecksum(nodeContent);
//
//        ServiceResponseStatus status;
//        if (request.getRevision().equals(currentChecksum)) {
//            status = SUCCESSFUL_UPDATE;
//
//            applyChanges(nodeContent, info);
//            contentRepository.updateInfo(path, info);
//
//            if (logger.isDebugEnabled())
//                logger.debug("Modified site node has been updated.");
//        } else {
//            status = FAILED_CONCURRENT_UPDATE;
//
//            SiteNodeContent currentContent = convert(info);
//            siteNode.setContent(currentContent);
//
//            if (logger.isDebugEnabled())
//                logger.debug("Modified site node is not updated. Current checksum is '{}'.", currentChecksum);
//        }
//
//        return new ServiceBeanResponse<>(siteNode, currentChecksum, status);
//    }
//
//    @RequestMapping(value = "/siteNode", method = RequestMethod.PUT)
//    public ServiceResponse addSiteNode(
//            @PathVariable String project,
//            @RequestParam(RequestParameters.LANGUAGE) String language,
//            @RequestBody SiteNode siteNode
//    ) {
//        // TODO: implement me!
//        return new ServiceResponse(SUCCESSFUL_CREATE);
//    }
//
//    @RequestMapping(value = "/siteNodes", method = RequestMethod.PUT)
//    public ServiceBeanResponse<SiteStructure> addSiteNodes(
//            @PathVariable String project,
//            @RequestParam(RequestParameters.LANGUAGE) String language,
//            @RequestBody SiteNodes request
//    ) {
//        SiteStructure siteStructure = new SiteStructure();
//        siteStructure.setStructure(request.getStructure());
//
////        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
////
////        contentRepository.
//
//        // TODO: implement me!
//        return new ServiceBeanResponse<>(siteStructure, ChecksumGenerator.getChecksum(siteStructure), SUCCESSFUL_CREATE);
//    }

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

//    private void applyChanges(SiteNodeContent nodeContent, SiteNodeInfo info) {
//        switch (nodeContent.getType()) {
//            case PAGE:
//                ((PageInfo) info).setTemplateReference(nodeContent.getTemplateReference());
//                break;
//            case DIRECTORY:
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown node type '" + nodeContent.getType() + "'!");
//        }
//
//        info.setName(nodeContent.getName());
//        info.setDisplayName(nodeContent.getDisplayName());
//        info.setLocalizedName(nodeContent.getLocalizedName());
//    }

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
}
