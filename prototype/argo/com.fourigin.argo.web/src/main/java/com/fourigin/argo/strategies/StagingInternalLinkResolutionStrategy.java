package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodes;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;

public class StagingInternalLinkResolutionStrategy implements InternalLinkResolutionStrategy {
    private FilenameStrategy filenameStrategy;

    private ContentRepositoryFactory contentRepositoryFactory;

    public StagingInternalLinkResolutionStrategy(ContentRepositoryFactory contentRepositoryFactory) {
        this.filenameStrategy = new DefaultFilenameStrategy(true); // TODO: externalize!
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Override
    public String resolveLink(String customer, String base, String nodePath) {
        ContentRepository contentRepository = contentRepositoryFactory.getInstance(customer, base);
        SiteNodeInfo nodeInfo = contentRepository.resolveInfo(SiteNodeInfo.class, nodePath);
        String targetPath = SiteNodes.getDefaultTarget(nodeInfo);
        PageInfo info = contentRepository.resolveInfo(PageInfo.class, targetPath);

        String folder = filenameStrategy.getFolder(base, info);
        if (folder.endsWith("/")) {
            folder = folder.substring(0, folder.length() - 1);
        }

        String file = filenameStrategy.getFilename(base, info);
        if (file.startsWith("/")) {
            file = file.substring(1);
        }

        return folder + '/' + file + ".html";
    }
}
