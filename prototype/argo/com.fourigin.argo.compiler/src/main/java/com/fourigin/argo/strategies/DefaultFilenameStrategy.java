package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
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

    public String getFilename(SiteNodeInfo info) {
        if (logger.isDebugEnabled()) logger.debug("Resolving filename of {}", info);

        if (info instanceof PageInfo) {
            PageInfo page = (PageInfo) info;
            return resolvePageFilename(page);
        }

        // TODO: implement reference nodes
//        if(info instanceof InternalReference) {
//            return getFilename(SdtNodes.resolveInternalReference((InternalReference) info));
//        }

        if (info instanceof DirectoryInfo) {
            return getFilename(((DirectoryInfo) info).getDefaultTarget());
        }

        return null;
    }

    private String resolvePageFilename(PageInfo page) {
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
            if (page.equals(parent.getDefaultTarget())) {
                return "index";
            }
        }

        return page.getLocalizedName();
    }

    public String getFolder(SiteNodeInfo info) {
        if (logger.isDebugEnabled()) logger.debug("Resolving folder of {}", info);

        if (info instanceof PageInfo) {
            return resolvePageFolder((PageInfo) info);
        }

        if (info instanceof DirectoryInfo) {
            return resolveDirectoryFolder((DirectoryInfo) info);
        }

        // TODO: implement reference nodes
//        if (info instanceof InternalReference) {
//            return getFolder(SdtNodes.resolveInternalReference((InternalReference) info));
//        }

        return null;
    }

    private String getInternalFolderPath(SiteNodeContainerInfo container) {
        if (container.getParent() == null) {
            // root
            return "/";
        }

        SiteNodeInfo node = (SiteNodeInfo) container;
        SiteNodeContainerInfo parent = node.getParent();
        String path = node.getLocalizedName();

        if (!(node instanceof DirectoryInfo)) {
            return null;
        }

//        DirectoryInfo directory = (DirectoryInfo) node;
//        if (directory.isVirtual()) {
//            return getInternalFolderPath(parent) + path + "-";
//        }

        return getInternalFolderPath(parent) + path + "/";
    }

    private String resolveDirectoryFolder(DirectoryInfo parent) {
        String result = getInternalFolderPath(parent);
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

        if (result != null && !result.equals("/") && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    private String resolvePageFolder(PageInfo page) {
        SiteNodeContainerInfo parent = page.getParent();

        if (parent == null) {
            return "/";
        }

        return resolveDirectoryFolder((DirectoryInfo) parent);
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
