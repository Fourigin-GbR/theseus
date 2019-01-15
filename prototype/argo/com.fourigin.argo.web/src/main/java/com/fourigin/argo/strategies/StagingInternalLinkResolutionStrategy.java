package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.PageInfo;
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
        PageInfo info = contentRepository.resolveInfo(PageInfo.class, nodePath);

        String folder = filenameStrategy.getFolder(base, info);
        String file = filenameStrategy.getFilename(base, info);

        return folder + '/' + file + ".html";
    }
}
