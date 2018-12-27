package com.fourigin.argo.models.structure.nodes;

import java.util.Locale;
import java.util.Map;

public final class SiteNodes {
    private SiteNodes() {
    }

    public static String resolveContent(String base, Map<String, String> data) {
        if (data == null) {
            return null;
        }

        String result = data.get(base.toLowerCase(Locale.US));
        if(result == null){
            result = data.get("");
        }

        return result;
    }
}
