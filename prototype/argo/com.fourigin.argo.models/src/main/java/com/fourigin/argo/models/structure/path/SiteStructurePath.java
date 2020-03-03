package com.fourigin.argo.models.structure.path;

import com.fourigin.argo.models.InvalidSiteStructurePathException;
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public final class SiteStructurePath {

    private List<String> steps;

    private SiteNodeInfo nodeInfo;

    public static SiteStructurePath forPath(String path, SiteNodeContainerInfo parent){
        return new SiteStructurePath(path, parent);
    }

    private SiteStructurePath(String path, SiteNodeContainerInfo parent) {
        Objects.requireNonNull(path, "Path must not be null!");
        Objects.requireNonNull(parent, "Parent must not be null!");

        steps = new ArrayList<>();

        List<SiteNodeInfo> nodes = parent.getNodes();

        SiteNodeInfo nodeInfo;

        if("/".equals(path) || "".equals(path)) {
            nodeInfo = parent;
        }
        else {
            nodeInfo = null;

            StringTokenizer tok = new StringTokenizer(path, "/");
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken().trim();
                if (token.isEmpty()) {
                    continue;
                }

                if (nodes != null) {
                    nodeInfo = null;

                    for (SiteNodeInfo node : nodes) {
                        if (token.equals(node.getName())) {
                            nodeInfo = node;
                            break;
                        }
                    }
                }

                if (nodeInfo == null) {
                    throw new InvalidSiteStructurePathException(path, token);
                }

                steps.add(token);

                if(nodeInfo instanceof DirectoryInfo) {
                    nodes = ((DirectoryInfo) nodeInfo).getNodes();
                }
            }
        }

        this.nodeInfo = nodeInfo;
    }

    public SiteNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public boolean isDirectory(){
        return nodeInfo instanceof DirectoryInfo;
    }

    public boolean isPage(){
        return nodeInfo instanceof PageInfo;
    }
}
