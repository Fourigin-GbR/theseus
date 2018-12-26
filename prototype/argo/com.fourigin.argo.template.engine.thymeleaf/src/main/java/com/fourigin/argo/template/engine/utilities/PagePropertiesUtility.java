package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PagePropertiesUtility implements SiteAttributesAwareThymeleafTemplateUtility, ProcessingModeAwareThymeleafTemplateUtility {

    private String compilerBase;

    private Map<String, String> siteAttributes;

    private ProcessingMode processingMode;

    private InternalLinkResolutionStrategy internalLinkResolutionStrategy;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement();

    private static final String BASE_URL_PREFIX = "base_url.";

    private final Logger logger = LoggerFactory.getLogger(PagePropertiesUtility.class);

    public String getPath(String nodePath){
        String baseUrlAttributeName = BASE_URL_PREFIX + processingMode.name();
        if (logger.isDebugEnabled()) logger.debug("Searching for site-attribute '{}'.", baseUrlAttributeName);

        String baseUrl = siteAttributes.get(baseUrlAttributeName);
        if (logger.isDebugEnabled()) logger.debug("Value of site-attribute '{}': '{}'.", baseUrlAttributeName, baseUrl);

        String linkPath = internalLinkResolutionStrategy.resolveLink(nodePath, compilerBase);

        return propertiesReplacement.process(baseUrl + linkPath, "base", compilerBase);
    }

    // *** getters / setters ***

    @Override
    public void setCompilerBase(String base) {
        this.compilerBase = base;
    }

    @Override
    public void setSiteAttributes(Map<String, String> siteAttributes) {
        this.siteAttributes = siteAttributes;
    }

    @Override
    public void setProcessingMode(ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public void setInternalLinkResolutionStrategy(InternalLinkResolutionStrategy internalLinkResolutionStrategy) {
        this.internalLinkResolutionStrategy = internalLinkResolutionStrategy;
    }
}
