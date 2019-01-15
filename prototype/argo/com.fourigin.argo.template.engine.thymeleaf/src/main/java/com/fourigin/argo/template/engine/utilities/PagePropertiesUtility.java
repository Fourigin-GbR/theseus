package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PagePropertiesUtility implements SiteAttributesAwareThymeleafTemplateUtility, ProcessingModeAwareThymeleafTemplateUtility, CustomerAwareThymeleafTemplateUtility {

    private String customer;

    private String compilerBase;

    private Map<String, String> siteAttributes;

    private Set<String> serviceNames;

    private ProcessingMode processingMode;

    private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement();

    private static final String BASE_URL_PREFIX = "base_url.";

    private static final String BASE_SERVICE_URL_PREFIX = "service_url.";

    private final Logger logger = LoggerFactory.getLogger(PagePropertiesUtility.class);

    public String getPath(String nodePath) {
        return getPath(nodePath, compilerBase, processingMode.name());
    }

    public String getPath(String nodePath, String externalCompilerBase) {
        return getPath(nodePath, externalCompilerBase, processingMode.name());
    }

    public String getPath(String nodePath, String externalCompilerBase, String externalProcessingMode) {
        String baseUrlAttributeName = BASE_URL_PREFIX + externalProcessingMode;
        if (logger.isDebugEnabled()) logger.debug("Searching for site-attribute '{}'.", baseUrlAttributeName);

        String baseUrl = siteAttributes.get(baseUrlAttributeName);
        if (logger.isDebugEnabled()) logger.debug("Value of site-attribute '{}': '{}'.", baseUrlAttributeName, baseUrl);

        InternalLinkResolutionStrategy linkResolutionStrategy = internalLinkResolutionStrategies.get(ProcessingMode.valueOf(externalProcessingMode));
        String linkPath = linkResolutionStrategy.resolveLink(customer, externalCompilerBase, nodePath);
        if (logger.isDebugEnabled()) logger.debug("Resolved internal link for path '{}': '{}'", nodePath, linkPath);

        return propertiesReplacement.process(baseUrl + linkPath, "base", externalCompilerBase);
    }

    public Set<String> getServiceNames() {
        return serviceNames;
    }

    public String getServicePath(String serviceName) {
        return getServicePath(serviceName, compilerBase, processingMode.name());
    }

    public String getServicePath(String serviceName, String externalProcessingMode) {
        return getServicePath(serviceName, compilerBase, externalProcessingMode);
    }

    public String getServicePath(String serviceName, String externalCompilerBase, String externalProcessingMode) {
        String baseUrlAttributeName = BASE_SERVICE_URL_PREFIX + serviceName + '.' + externalProcessingMode;
        if (logger.isDebugEnabled()) logger.debug("Searching for service site-attribute '{}'.", baseUrlAttributeName);

        String serviceUrl = siteAttributes.get(baseUrlAttributeName);
        if (logger.isDebugEnabled())
            logger.debug("Value of service site-attribute '{}': '{}'.", baseUrlAttributeName, serviceUrl);

        if (serviceUrl == null) {
            return null;
        }

        return propertiesReplacement.process(serviceUrl, "base", externalCompilerBase);
    }

    // *** getters / setters ***

    @Override
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public void setCompilerBase(String base) {
        this.compilerBase = base;
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
}
