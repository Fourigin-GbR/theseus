package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodes;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PagePropertiesUtility implements
    SiteAttributesAwareThymeleafTemplateUtility,
    ProcessingModeAwareThymeleafTemplateUtility,
    ProjectAwareThymeleafTemplateUtility,
    RootSiteNodeAwareThymeleafTemplateUtility {

    private String project;

    private String language;

    private Map<String, String> siteAttributes;

    private Set<String> serviceNames;

    private ProcessingMode processingMode;

    private SiteNodeContainerInfo rootSiteNodeContainer;

    private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement();

    private static final String BASE_URL_PREFIX = "base_url.";

    private static final String BASE_SERVICE_URL_PREFIX = "service_url.";

    private final Logger logger = LoggerFactory.getLogger(PagePropertiesUtility.class);

    public String getPath(String nodePath) {
        return getPath(nodePath, language, processingMode.name());
    }

    public String getPath(String nodePath, String language) {
        return getPath(nodePath, language, processingMode.name());
    }

    public String getPath(String nodePath, String language, String externalProcessingMode) {
        String baseUrl = getNodePathAttributeValue(language, externalProcessingMode);
        if (logger.isDebugEnabled())
            logger.debug("Value of base path site-attribute for '{}'.'{}': '{}'", language, externalProcessingMode, baseUrl);

        InternalLinkResolutionStrategy linkResolutionStrategy = internalLinkResolutionStrategies.get(
            ProcessingMode.valueOf(externalProcessingMode)
        );

        String linkPath = linkResolutionStrategy.resolveLink(project, language, nodePath);
        if (logger.isDebugEnabled()) logger.debug("Resolved internal link for path '{}': '{}'", nodePath, linkPath);

        String pattern;
        switch (ProcessingMode.valueOf(externalProcessingMode)) {
            case CMS:
                pattern = baseUrl + linkPath;
                break;
            default:
                pattern = linkPath;
                break;
        }

        return propertiesReplacement.process(pattern, "language", language);
    }

    public String getAbsolutePath(String nodePath) {
        return getAbsolutePath(nodePath, language, processingMode.name());
    }

    public String getAbsolutePath(String nodePath, String language) {
        return getAbsolutePath(nodePath, language, processingMode.name());
    }

    public String getAbsolutePath(String nodePath, String language, String mode) {
        String baseUrl = getNodePathAttributeValue(language, mode);
        if (logger.isDebugEnabled())
            logger.debug("Value of base path site-attribute for '{}'.'{}': '{}'", language, mode, baseUrl);

        InternalLinkResolutionStrategy linkResolutionStrategy = internalLinkResolutionStrategies.get(ProcessingMode.valueOf(mode));
        String linkPath = linkResolutionStrategy.resolveLink(project, language, nodePath);
        if (logger.isDebugEnabled()) logger.debug("Resolved internal link for path '{}': '{}'", nodePath, linkPath);

        String pattern = baseUrl + linkPath;

        return propertiesReplacement.process(pattern, "language", language);
    }

    public List<NodeDescriptor> getAncestors(String nodePath) {
        return getAncestors(nodePath, language);
    }

    public List<NodeDescriptor> getAncestors(String nodePath, String language) {
        List<NodeDescriptor> result = new ArrayList<>();

        SiteNodeContainerInfo currentNodeInfo = rootSiteNodeContainer;

        if (nodePath.startsWith("/")) {
            nodePath = nodePath.substring(1);
        }
        if (nodePath.endsWith("/")) {
            nodePath = nodePath.substring(0, nodePath.length() - 1);
        }

        List<String> foundNodeNames = new ArrayList<>();
        String[] pathSteps = nodePath.split("/");
        for (int i = 0; i < pathSteps.length - 1; i++) {
            String pathStep = pathSteps[i];
            List<SiteNodeInfo> nodes = currentNodeInfo.getNodes();
            SiteNodeInfo matchingNode = null;
            for (SiteNodeInfo node : nodes) {
                String name = node.getName();
                foundNodeNames.add(name);
                if (pathStep.equals(name)) {
                    matchingNode = node;
                    break;
                }
            }

            if (matchingNode == null) {
                Collections.sort(foundNodeNames);
                throw new IllegalArgumentException("Unable to fine node for name '" + pathStep + "'! Candidates are: " + foundNodeNames);
            }

            NodeDescriptor nodeDescriptor = new NodeDescriptor();
            nodeDescriptor.setName(SiteNodes.resolveContent(language, matchingNode.getDisplayName()));
            nodeDescriptor.setPath(SiteNodes.getDefaultTarget(matchingNode));
            result.add(nodeDescriptor);

            if (!SiteNodeContainerInfo.class.isAssignableFrom(matchingNode.getClass())) {
                throw new IllegalArgumentException("Unable to resolve ancestors! Expecting a directory but found a page for name '" + matchingNode.getName() + "'!");
            }

            currentNodeInfo = (SiteNodeContainerInfo) matchingNode;
        }

        return result;
    }

    private String getNodePathAttributeValue(String language, String mode) {
        {
            String attrName = BASE_URL_PREFIX + language.toUpperCase(Locale.US) + '.' + mode;
            if (siteAttributes.containsKey(attrName)) {
                if (logger.isDebugEnabled())
                    logger.debug("Returning language & mode specific attribute value for '{}'.", attrName);
                return siteAttributes.get(attrName);
            }
        }

        {
            String attrName = BASE_URL_PREFIX + mode;
            if (siteAttributes.containsKey(attrName)) {
                if (logger.isDebugEnabled())
                    logger.debug("Returning mode specific attribute value for '{}'.", attrName);
                return siteAttributes.get(attrName);
            }
        }

        throw new IllegalStateException("No base path site attribute found for language '" + language + "' and mode '" + mode + "'!");
    }

    public Set<String> getServiceNames() {
        return serviceNames;
    }

    public String getServicePath(String serviceName) {
        return getServicePath(serviceName, language, processingMode.name());
    }

    public String getServicePath(String serviceName, String mode) {
        return getServicePath(serviceName, language, mode);
    }

    public String getServicePath(String serviceName, String language, String mode) {
        String baseUrlAttributeName = BASE_SERVICE_URL_PREFIX + serviceName + '.' + mode;
        if (logger.isDebugEnabled()) logger.debug("Searching for service site-attribute '{}'.", baseUrlAttributeName);

        String serviceUrl = siteAttributes.get(baseUrlAttributeName);
        if (logger.isDebugEnabled())
            logger.debug("Value of service site-attribute '{}': '{}'.", baseUrlAttributeName, serviceUrl);

        if (serviceUrl == null) {
            return null;
        }

        return propertiesReplacement.process(serviceUrl, "language", language);
    }

    // *** getters / setters ***

    @Override
    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setSiteAttributes(Map<String, String> siteAttributes) {
        this.siteAttributes = siteAttributes;

        this.serviceNames = new HashSet<>();
        int startPos = BASE_SERVICE_URL_PREFIX.length() + 1;

        for (Map.Entry<String, String> entry : siteAttributes.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(BASE_SERVICE_URL_PREFIX)) {
                int endPos = key.indexOf(".", startPos);
                String serviceName = key.substring(startPos, endPos);
                serviceNames.add(serviceName);
            }
        }
    }

    @Override
    public void setProcessingMode(ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public void setInternalLinkResolutionStrategies(Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies) {
        this.internalLinkResolutionStrategies = internalLinkResolutionStrategies;
    }

    @Override
    public void setRootSiteNode(SiteNodeContainerInfo root) {
        this.rootSiteNodeContainer = root;
    }
}
