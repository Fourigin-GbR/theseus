package com.fourigin.argo.controller.system.models;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;

public class ArgoSystem {
    private String project;
    private String language;
    private SiteNodeContainerInfo root;

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

    public SiteNodeContainerInfo getRoot() {
        return root;
    }

    public void setRoot(SiteNodeContainerInfo root) {
        this.root = root;
    }

    public static class Builder {
        private String project;
        private String language;
        private SiteNodeContainerInfo root;

        public Builder withProject(String project){
            this.project = project;
            return this;
        }

        public Builder withLanguage(String language){
            this.language = language;
            return this;
        }

        public Builder withRoot(SiteNodeContainerInfo root){
            this.root = root;
            return this;
        }

        public ArgoSystem build(){
            ArgoSystem system = new ArgoSystem();

            system.setProject(project);
            system.setLanguage(language);
            system.setRoot(root);

            return system;
        }
    }
}
