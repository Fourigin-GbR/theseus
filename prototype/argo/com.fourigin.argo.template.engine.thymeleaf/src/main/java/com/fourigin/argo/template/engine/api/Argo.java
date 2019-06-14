package com.fourigin.argo.template.engine.api;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.argo.template.engine.utilities.ContentElementUtility;
import com.fourigin.argo.template.engine.utilities.FormatterUtility;
import com.fourigin.argo.template.engine.utilities.PagePropertiesUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Argo {
    private String project;
    private String language;
    private String path;
    private ContentPage contentPage;
    private PageInfo pageInfo;
    private ProcessingMode processingMode;
    private Map<String, String> siteAttributes;

    private ContentElementUtility contentElementUtility;

    private PagePropertiesUtility pagePropertiesUtility;

    private FormatterUtility formatterUtility;

    private Map<String, ThymeleafTemplateUtility> customUtilities;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ContentPage getContentPage() {
        return contentPage;
    }

    public void setContentPage(ContentPage contentPage) {
        this.contentPage = contentPage;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public ProcessingMode getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public Map<String, String> getSiteAttributes() {
        return siteAttributes;
    }

    public void setSiteAttributes(Map<String, String> siteAttributes) {
        this.siteAttributes = siteAttributes;
    }

    public ContentElementUtility getContentElementUtility() {
        return contentElementUtility;
    }

    public void setContentElementUtility(ContentElementUtility contentElementUtility) {
        this.contentElementUtility = contentElementUtility;
    }

    public PagePropertiesUtility getPagePropertiesUtility() {
        return pagePropertiesUtility;
    }

    public void setPagePropertiesUtility(PagePropertiesUtility pagePropertiesUtility) {
        this.pagePropertiesUtility = pagePropertiesUtility;
    }

    public FormatterUtility getFormatterUtility() {
        return formatterUtility;
    }

    public void setFormatterUtility(FormatterUtility formatterUtility) {
        this.formatterUtility = formatterUtility;
    }

    public Map<String, ThymeleafTemplateUtility> getCustomUtilities() {
        return customUtilities;
    }

    public void setCustomUtilities(Map<String, ThymeleafTemplateUtility> customUtilities) {
        this.customUtilities = customUtilities;
    }

    public void addCustomUtility(String name, ThymeleafTemplateUtility utility) {
        if (utility == null) {
            return;
        }

        Objects.requireNonNull(name, "Utility name must not be null!");

        if (customUtilities == null) {
            customUtilities = new HashMap<>();
        }

        customUtilities.put(name, utility);
    }

    public static class Builder {
        private String project;
        private String language;
        private String path;
        private ContentPage contentPage;
        private PageInfo pageInfo;
        private ProcessingMode processingMode;
        private Map<String, String> siteAttributes;
        private Map<String, ThymeleafTemplateUtility> customUtilities = new HashMap<>();
        private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies;
        private SiteNodeContainerInfo root;

        public Builder withProject(String project) {
            this.project = project;
            return this;
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withContentPage(ContentPage contentPage) {
            this.contentPage = contentPage;
            return this;
        }

        public Builder withPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
            return this;
        }

        public Builder withProcessingMode(ProcessingMode processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public Builder withSiteAttributes(Map<String, String> siteAttributes) {
            this.siteAttributes = siteAttributes;
            return this;
        }

        public Builder withInternalLinkResolutionStrategies(Map<ProcessingMode, InternalLinkResolutionStrategy> strategies) {
            this.internalLinkResolutionStrategies = strategies;
            return this;
        }

        public Builder withCustomUtility(String name, ThymeleafTemplateUtility utility) {
            this.customUtilities.put(name, utility);
            return this;
        }

        public Builder withRootNodeInfo(SiteNodeContainerInfo root) {
            this.root = root;
            return this;
        }

        public Argo build() {
            Argo argo = new Argo();
            argo.setProject(this.project);
            argo.setLanguage(this.language);
            argo.setPath(this.path);
            argo.setContentPage(this.contentPage);
            argo.setProcessingMode(this.processingMode);
            argo.setPageInfo(this.pageInfo);
            argo.setSiteAttributes(this.siteAttributes);

            ContentElementUtility contentUtility = new ContentElementUtility();
            contentUtility.setLanguage(this.language);
            contentUtility.setContentPage(this.contentPage);
            argo.setContentElementUtility(contentUtility);

            PagePropertiesUtility pagePropertiesUtility = new PagePropertiesUtility();
            pagePropertiesUtility.setProject(this.project);
            pagePropertiesUtility.setLanguage(this.language);
            pagePropertiesUtility.setSiteAttributes(this.siteAttributes);
            pagePropertiesUtility.setProcessingMode(this.processingMode);
            pagePropertiesUtility.setInternalLinkResolutionStrategies(this.internalLinkResolutionStrategies);
            pagePropertiesUtility.setRootSiteNode(this.root);
            argo.setPagePropertiesUtility(pagePropertiesUtility);

            FormatterUtility formatterUtility = new FormatterUtility();
            formatterUtility.setLanguage(this.language);
            argo.setFormatterUtility(formatterUtility);

            if (!this.customUtilities.isEmpty()) {
                argo.setCustomUtilities(this.customUtilities);
            }

            return argo;
        }
    }
}
