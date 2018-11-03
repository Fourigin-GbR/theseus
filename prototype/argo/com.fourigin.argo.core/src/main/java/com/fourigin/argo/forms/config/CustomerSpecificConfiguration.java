package com.fourigin.argo.forms.config;

import java.util.Map;

public class CustomerSpecificConfiguration {
    private Map<String, String> assetsLoadBalancerRoot;
    private Map<String, String> assetsDomain;
    private Map<String, Map<String, String>> documentRoots;

    public Map<String, String> getAssetsLoadBalancerRoot() {
        return assetsLoadBalancerRoot;
    }

    public void setAssetsLoadBalancerRoot(Map<String, String> assetsLoadBalancerRoot) {
        this.assetsLoadBalancerRoot = assetsLoadBalancerRoot;
    }

    public Map<String, String> getAssetsDomain() {
        return assetsDomain;
    }

    public void setAssetsDomain(Map<String, String> assetsDomain) {
        this.assetsDomain = assetsDomain;
    }

    public Map<String, Map<String, String>> getDocumentRoots() {
        return documentRoots;
    }

    public void setDocumentRoots(Map<String, Map<String, String>> documentRoots) {
        this.documentRoots = documentRoots;
    }

    @Override
    public String toString() {
        return "CustomerSpecificConfiguration{" +
            "assetsLoadBalancerRoot=" + assetsLoadBalancerRoot +
            ", assetsDomain=" + assetsDomain +
            ", documentRoots=" + documentRoots +
            '}';
    }
}
