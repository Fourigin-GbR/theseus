package com.fourigin.argo.forms.config;

import java.util.Map;
import java.util.Set;

public class ProjectSpecificConfiguration {
    private Map<String, Set<String>> bases;
//    private Map<String, String> assetsLoadBalancerRoot;
    private Map<String, String> assetsDomain;
//    private Map<String, Map<String, String>> documentRoots;

    public Map<String, Set<String>> getBases() {
        return bases;
    }

    public void setBases(Map<String, Set<String>> customerBases) {
        this.bases = customerBases;
    }

    public Map<String, String> getAssetsDomain() {
        return assetsDomain;
    }

    public void setAssetsDomain(Map<String, String> assetsDomain) {
        this.assetsDomain = assetsDomain;
    }

    @Override
    public String toString() {
        return "ProjectSpecificConfiguration{" +
            "bases=" + bases +
            ", assetsDomain=" + assetsDomain +
            '}';
    }
}
