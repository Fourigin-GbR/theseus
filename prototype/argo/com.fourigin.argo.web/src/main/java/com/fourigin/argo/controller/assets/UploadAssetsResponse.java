package com.fourigin.argo.controller.assets;

import com.fourigin.argo.assets.models.Asset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UploadAssetsResponse implements Serializable {
    private static final long serialVersionUID = -6081563516514105371L;

    private Map<String, String> successful;
    private Map<String, Throwable> failed;

    public void registerSuccess(Asset asset){
        if(successful == null){
            successful = new HashMap<>();
        }

        successful.put(asset.getName(), asset.getId());
    }

    public void registerFail(String name, Throwable cause){
        if(failed == null){
            failed = new HashMap<>();
        }

        failed.put(name, cause);
    }

    public Map<String, String> getSuccessful() {
        return successful;
    }

    public void setSuccessful(Map<String, String> successful) {
        this.successful = successful;
    }

    public Map<String, Throwable> getFailed() {
        return failed;
    }

    public void setFailed(Map<String, Throwable> failed) {
        this.failed = failed;
    }
}
