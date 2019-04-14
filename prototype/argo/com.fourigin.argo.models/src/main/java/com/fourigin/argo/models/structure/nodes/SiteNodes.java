package com.fourigin.argo.models.structure.nodes;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SiteNodes {
    private SiteNodes() {
    }

    public static String resolveContent(String locale, Map<String, String> data) {
        if (data == null) {
            return null;
        }

        String result = data.get(locale.toLowerCase(Locale.US));
        if (result == null) {
            result = data.get("");
        }

        return result;
    }

    public static String getDefaultTarget(SiteNodeInfo node) {
        if (node == null) {
            return null;
        }

        String parentPath = node.getPath();
        if (!SiteNodeContainerInfo.class.isAssignableFrom(node.getClass())) {
            if ("/".equals(parentPath)) {
                // root level node
                return '/' + node.getName();
            }

            // sub level node
            return node.getPath() + '/' + node.getName();
        }

        // node container
        SiteNodeContainerInfo containerNode = (SiteNodeContainerInfo) node;
        List<SiteNodeInfo> subNodes = containerNode.getNodes();
        if (subNodes == null || subNodes.isEmpty()) {
            throw new IllegalArgumentException("Unable to resolve default target of an empty directory '" + node.getName() + "'!");
        }

        return getDefaultTarget(subNodes.get(0));
    }
}
