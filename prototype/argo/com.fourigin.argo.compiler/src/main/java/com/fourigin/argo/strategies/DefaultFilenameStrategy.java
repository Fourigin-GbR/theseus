package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFilenameStrategy implements FilenameStrategy {
    private final Logger logger = LoggerFactory.getLogger(DefaultFilenameStrategy.class);

    private boolean replacingDefaultTargetWithIndex;

    public DefaultFilenameStrategy() {
        this(true);
    }

    public DefaultFilenameStrategy(boolean replacingDefaultTargetWithIndex) {
        this.replacingDefaultTargetWithIndex = replacingDefaultTargetWithIndex;
    }

    public String getFilename(String base, SiteNodeInfo info) {
        if (logger.isDebugEnabled()) logger.debug("Resolving filename of {}", info);

        if (info instanceof PageInfo) {
            PageInfo page = (PageInfo) info;
            return resolvePageFilename(base, page);
        }

        // TODO: implement reference nodes
//        if(info instanceof InternalReference) {
//            return getFilename(SdtNodes.resolveInternalReference((InternalReference) info));
//        }

        if (info instanceof DirectoryInfo) {
            return SiteNodes.resolveContent(base, info.getLocalizedName());
        }

        return null;
    }

    private String resolvePageFilename(String base, PageInfo page) {
//      virtual directory logic
//        List<String> stack = new ArrayList<>();
//
//        SiteNodeInfo current = page;
//        for (; ; ) {
//            SiteNodeContainerInfo parent = current.getParent();
//            if (!(parent instanceof DirectoryInfo)) {
//                break;
//            }
//
//            DirectoryInfo dir = (DirectoryInfo) parent;
//            if (dir.isVirtual()) {
//                stack.add(0, dir.getLocalizedName());
//                current = dir;
//            }
//        }
//
//        if (!stack.isEmpty()) {
//            // means that there was a virtual directory in the parent hierarchy
//            stack.add(page.getLocalizedName());
//            StringBuilder builder = new StringBuilder();
//            boolean isFirst = true;
//            for (String currentFilenamePart : stack) {
//                if (isFirst) {
//                    isFirst = false;
//                } else {
//                    builder.append("-");
//                }
//                builder.append(currentFilenamePart);
//            }
//            return builder.toString();
//        }

        if (replacingDefaultTargetWithIndex) {
            SiteNodeContainerInfo parent = page.getParent();
            if (logger.isDebugEnabled()) logger.debug("Parent: '{}'", parent);

            SiteNodeInfo defaultTarget = parent.getDefaultTarget();
            if (page.equals(defaultTarget)) {
                if (logger.isDebugEnabled()) logger.debug("Returning 'index' as default target.");
                return "index";
            }

            if (logger.isDebugEnabled()) logger.debug("Not a default target '{}'.", defaultTarget);
        }

        if (logger.isDebugEnabled()) logger.debug("Returning localized name of page '{}'.", page);
        String fileName = SiteNodes.resolveContent(base, page.getLocalizedName());
        if(fileName == null){
            String id = page.getPath() + ">" + page.getName();
            if (logger.isWarnEnabled()) logger.warn("Unable to resolve localized node path of '{}', using node name as fallback!", id);
            fileName = page.getName();
        }

        return fileName;
    }

    public String getFolder(String base, SiteNodeInfo info) {
        if (logger.isDebugEnabled()) logger.debug("Resolving folder of {}", info);

        if (info instanceof PageInfo) {
            return resolvePageFolder(base, (PageInfo) info);
        }

        if (info instanceof DirectoryInfo) {
            return resolveDirectoryFolder(base, (DirectoryInfo) info);
        }

        // TODO: implement reference nodes
//        if (info instanceof InternalReference) {
//            return getFolder(SdtNodes.resolveInternalReference((InternalReference) info));
//        }

        return null;
    }

    private String getInternalFolderPath(String base, SiteNodeContainerInfo container) {
        if (container == null) {
            // root
            return "/";
        }

        String id = container.getPath() + ">" + container.getName();

        if (!(container instanceof DirectoryInfo)) {
            return null;
        }

        SiteNodeContainerInfo parent = container.getParent();
        String path = SiteNodes.resolveContent(base, container.getLocalizedName());
        if(path == null){
            if (logger.isWarnEnabled()) logger.warn("Unable to resolve localized node path of '{}', using node name as fallback!", id);
            path = container.getName();
        }

        if (parent == null || path == null) {
            return getInternalFolderPath(base, parent);
        }

        return getInternalFolderPath(base, parent) + path + "/";
    }

    private String resolveDirectoryFolder(String base, SiteNodeContainerInfo container) {
        String result = getInternalFolderPath(base, container.getParent());
        if (logger.isDebugEnabled())
            logger.debug("folder path is '{}'.", result);

        // virtual directories handling
//        if (result != null && result.endsWith("-")) {
//            int idx = result.lastIndexOf("/");
//            if (-1 == idx) {
//                if (logger.isWarnEnabled())
//                    logger.warn("Incomplete virtual site-path '{}'. '/' is missing before '-'!", sitePath);
//            } else {
//                result = result.substring(0, idx);
//            }
//            if (!result.endsWith("/")) {
//                result = result + "/";
//            }
//        }
//
//        if (logger.isDebugEnabled())
//            logger.debug("final folder path is '{}'.", result);

        result = cleanup(result);
//        if (result != null && !"/".equals(result) && result.endsWith("/")) {
//            result = result.substring(0, result.length() - 1);
//        }

        return result;
    }

    private String cleanup(String path){
        if (path != null && !"/".equals(path) && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }

    private String resolvePageFolder(String base, PageInfo page) {
        SiteNodeContainerInfo parent = page.getParent();

        if (parent == null) {
            return "/";
        }

        return cleanup(getInternalFolderPath(base, parent));
    }

//    private String resolveSitePath(SiteNodeInfo node) {
//        if (node == null) {
//            return null;
//        }
//
//        List<SiteNodeContainerInfo> hierarchy = resolveParentHierarchy(node);
//        StringBuilder result = new StringBuilder();
//        for (SiteNodeContainerInfo container : hierarchy) {
//            if (container instanceof SiteNodeInfo) {
//                SiteNodeInfo info = (SiteNodeInfo) container;
//                String contentName = info.getName();
//                if (contentName == null) {
//                    if (logger.isDebugEnabled())
//                        logger.debug("Detected an SdtNode with neither english- nor localized-filename-part. Returning null.");
//                    return null;
//                }
//
////                if(!"/".equals(contentName)){
////                    result.append('/');
////                }
//                result.append(contentName);
//            }
//        }
//
//        String contentName = node.getName();
//        if (contentName == null) {
//            if (logger.isDebugEnabled())
//                logger.debug("Detected an SdtNode with neither english- nor localized-filename-part. Returning null.");
//            return null;
//        }
//
////        if(!"/".equals(contentName)){
////            result.append('/');
////        }
//        result.append(contentName);
//
//        return result.toString();
//    }
//
//    private List<SiteNodeContainerInfo> resolveParentHierarchy(SiteNodeInfo node) {
//        List<SiteNodeContainerInfo> hierarchy = new ArrayList<>();
//
//        SiteNodeContainerInfo parent = node.getParent();
//        while (parent != null) {
//            hierarchy.add(0, parent);
//            if (parent.getParent() == null) {
//                parent = null; // root
//            } else {
//                SiteNodeContainerInfo parentNode = parent;
//                parent = parentNode.getParent();
//            }
//        }
//        return hierarchy;
//    }
}
