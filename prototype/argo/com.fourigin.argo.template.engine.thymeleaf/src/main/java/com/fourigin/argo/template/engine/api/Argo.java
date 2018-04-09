package com.fourigin.argo.template.engine.api;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.template.engine.utilities.ContentElementUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Argo {
    private String base;
    private String path;
    private ContentPage contentPage;
    private PageInfo pageInfo;
    private Map<String, String> siteAttributes;

    private ContentElementUtility contentElementUtility;

    private Map<String, ThymeleafTemplateUtility> customUtilities;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
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

    public Map<String, ThymeleafTemplateUtility> getCustomUtilities() {
        return customUtilities;
    }

    public void setCustomUtilities(Map<String, ThymeleafTemplateUtility> customUtilities) {
        this.customUtilities = customUtilities;
    }

    public void addCustomUtility(String name, ThymeleafTemplateUtility utility){
        if(utility == null){
            return;
        }

        Objects.requireNonNull(name, "Utility name must not be null!");

        if(customUtilities == null){
            customUtilities = new HashMap<>();
        }

        customUtilities.put(name, utility);
    }

    public static class Builder {
        private String base;
        private String path;
        private ContentPage contentPage;
        private PageInfo pageInfo;
        private Map<String, String> siteAttributes;
        private Map<String, ThymeleafTemplateUtility> customUtilities = new HashMap<>();

        public Builder withBase(String base){
            this.base = base;
            return this;
        }

        public Builder withPath(String path){
            this.path = path;
            return this;
        }

        public Builder withContentPage(ContentPage contentPage){
            this.contentPage = contentPage;
            return this;
        }

        public Builder withPageInfo(PageInfo pageInfo){
            this.pageInfo = pageInfo;
            return this;
        }

        public Builder withSiteAttributes(Map<String, String> siteAttributes){
            this.siteAttributes = siteAttributes;
            return this;
        }

        public Builder withCustomUtility(String name, ThymeleafTemplateUtility utility){
            this.customUtilities.put(name, utility);
            return this;
        }

        public Argo build(){
            Argo argo = new Argo();
            argo.setBase(base);
            argo.setPath(path);
            argo.setContentPage(contentPage);
            argo.setPageInfo(pageInfo);
            argo.setSiteAttributes(siteAttributes);

            ContentElementUtility contentUtility = new ContentElementUtility();
            contentUtility.setCompilerBase(base);
            contentUtility.setContentPage(contentPage);
            argo.setContentElementUtility(contentUtility);

            if(!customUtilities.isEmpty()){
                argo.setCustomUtilities(customUtilities);
            }

            return argo;
        }
    }
}
