package com.fourigin.argo.controller.system;

import com.fourigin.argo.ServiceErrorResponse;
import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.template.engine.ContentPageCompilerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/{project}/system")
public class SystemController {

    private final Logger logger = LoggerFactory.getLogger(SystemController.class);

    private ContentRepositoryFactory contentRepositoryFactory;

    public SystemController(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView dashboard(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing system request for project '{}' and language '{}'.", project, language);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeContainerInfo root = contentRepository.resolveInfo(SiteNodeContainerInfo.class, "/");

        ModelAndView modelAndView = new ModelAndView("system");

        modelAndView.addObject("system", new ArgoSystem.Builder()
            .withProject(project)
            .withLanguage(language)
            .withRoot(root)
            .build()
        );

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public List<TreeItem> resolveTree(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing tree request for project '{}' and language '{}'.", project, language);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeContainerInfo root = contentRepository.resolveInfo(SiteNodeContainerInfo.class, "/");
        if (root == null) {
            return Collections.emptyList();
        }

        List<SiteNodeInfo> nodes = root.getNodes();
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<TreeItem> result = new ArrayList<>(nodes.size());
        for (SiteNodeInfo node : nodes) {
            result.add(convertNode(node));
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public TreeItemInfo resolveInfo(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language,
        @RequestParam(RequestParameters.PATH) String path
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing info request for project '{}', language '{}' and path '{}'.", project, language, path);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);

        TreeItemInfo result = new TreeItemInfo();
        result.setPath(path);
        result.setLocalizedName(getLocalizedValue(info.getLocalizedName(), language));
        result.setDisplayName(getLocalizedValue(info.getDisplayName(), language));
        result.setDescription(info.getDescription());

        if (!SiteNodeContainerInfo.class.isAssignableFrom(info.getClass())) {
            PageInfo pageInfo = PageInfo.class.cast(info);
            TemplateReference templateReference = pageInfo.getTemplateReference();
            if (templateReference != null) {
                StringBuilder builder = new StringBuilder();

                String templateId = templateReference.getTemplateId();
                if (templateId != null && !templateId.isEmpty()) {
                    builder.append(templateId);
                }

                String variation = templateReference.getVariationId();
                if (variation != null && !variation.isEmpty()) {
                    builder.append('.').append(variation);
                }

                String revision = templateReference.getRevision();
                if (revision != null && !revision.isEmpty()) {
                    builder.append(" (").append(revision).append(')');
                }
                result.setTemplateReference(builder.toString());
            }

            PageState pageState = contentRepository.resolvePageState(pageInfo);
            if (pageState != null) {
                result.setPageState(pageState);
            }
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateState", method = RequestMethod.GET)
    public UpdateResult resolveInfo(
        @PathVariable String project,
        @RequestParam(RequestParameters.LANGUAGE) String language,
        @RequestParam(RequestParameters.PATH) String path,
        @RequestParam("state") String state
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing updateState request for project '{}', language '{}' and path '{}'.", project, language, path);

        ContentRepository contentRepository = contentRepositoryFactory.getInstance(project, language);
        SiteNodeInfo info = contentRepository.resolveInfo(SiteNodeInfo.class, path);
        if (!PageInfo.class.isAssignableFrom(info.getClass())) {
            return UpdateResult.failed("Invalid site structure path '" + path + "'! Unable to update state of a non-page node!");
        }

        PageInfo pageInfo = (PageInfo) info;
        PageState pageState = contentRepository.resolvePageState(pageInfo);

        boolean changed = false;
        switch (state) {
            case "DRAFT":
                if (pageState.isStaged()) {
                    pageState.setStaged(false);
                    pageState.setLive(false);
                    pageState.setTimestampLiveSwitch(0);
                    CompileState compileState = pageState.getCompileState();
                    if (compileState != null) {
                        compileState.setTimestamp(0);
                        compileState.setChecksum(null);
                        pageState.setCompileState(compileState);
                    }
                    changed = true;
                }
                break;
            case "STAGED":
                if (!pageState.isStaged() || pageState.isLive()) {
//                    CompileState compileState = pageState.getCompileState();
//                    if (compileState == null || !compileState.isCompiled()) {
//                        return UpdateResult.failed("Node '" + path + "' is not in a compiled state!");
//                    }

//                    if (compileState.isCompiled()) {
                        pageState.setStaged(true);
                        pageState.setLive(false);
                        pageState.setTimestampLiveSwitch(0);
                        changed = true;
//                    }
                }
                break;
            case "LIVE":
                if (!pageState.isLive()) {
                    if (!pageState.isStaged()) {
                        return UpdateResult.failed("Node '" + path + "' is not staged! Only staged nodes can be switched live!");
                    }

                    pageState.setLive(true);
                    changed = true;
                }
                break;
            default:
                return UpdateResult.failed("Unsupported page state '" + state + "'! Possible values are 'DRAFT', 'STAGED' and 'LIVE'");
        }

        if (changed) {
            contentRepository.updatePageState(pageInfo, pageState);
        }

        return UpdateResult.ok();
    }

    private String getLocalizedValue(Map<String, String> values, String language) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.get(language);
    }

    private TreeItem convertNode(SiteNodeInfo info) {
        TreeItem item = new TreeItem();

        String id = info.getPath() + '/' + info.getName();
        id = id.replaceAll("//", "/");

        if (logger.isDebugEnabled()) logger.debug("Converting node {} ({})", id, info.getClass());

        item.setId(id);
        item.setText(info.getName());

        if (SiteNodeContainerInfo.class.isAssignableFrom(info.getClass())) {
            if (logger.isDebugEnabled()) logger.debug("... is a container node");

            SiteNodeContainerInfo containerInfo = SiteNodeContainerInfo.class.cast(info);
            List<SiteNodeInfo> nodes = containerInfo.getNodes();
            if (nodes == null || nodes.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("... is empty");

                item.setChildren(Collections.emptyList());
            } else {
                if (logger.isDebugEnabled()) logger.debug("... is not empty");

                List<TreeItem> children = new ArrayList<>(nodes.size());
                for (SiteNodeInfo node : nodes) {
                    children.add(convertNode(node));
                }
                item.setChildren(children);
            }
            item.setIcon("folder");
        } else {
            item.setIcon("file");
        }

        return item;
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
}
