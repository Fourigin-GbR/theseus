package com.fourigin.argo.controller.system;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;

public class ArgoSystem {
    private String customer;
    private String base;
    private SiteNodeContainerInfo root;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public SiteNodeContainerInfo getRoot() {
        return root;
    }

    public void setRoot(SiteNodeContainerInfo root) {
        this.root = root;
    }

    public static class Builder {
        private String customer;
        private String base;
        private SiteNodeContainerInfo root;

        public Builder withCustomer(String customer){
            this.customer = customer;
            return this;
        }

        public Builder withBase(String base){
            this.base = base;
            return this;
        }

        public Builder withRoot(SiteNodeContainerInfo root){
            this.root = root;
            return this;
        }

        public ArgoSystem build(){
            ArgoSystem system = new ArgoSystem();

            system.setCustomer(customer);
            system.setBase(base);
            system.setRoot(root);

            return system;
        }
    }
}
