package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.DirectoryInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.PageInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Playground {
    public static void main(String[] args) {
        SiteStructureResolver xResolver = null; //factory.getInstance("de_DE");
        ContentPageRepository cRepository = null;

        Map<String, String> siteAttributes = xResolver.resolveSiteAttributes();
        List<SiteNodeInfo> rootNodes = xResolver.resolveNode(DirectoryInfo.class, "/").getNodes();
        List<SiteNodeInfo> articles2016Absolute = xResolver.resolveNode(DirectoryInfo.class, "/articles/2016").getNodes();

        DirectoryInfo articlesDirectory = xResolver.resolveNode(DirectoryInfo.class, "/articles");
        List<SiteNodeInfo> articles2016Relative = xResolver.resolveNode(DirectoryInfo.class, articlesDirectory, "2016").getNodes();

        Collection<PageInfo> allNodes = xResolver.resolveNodes("/");
        Collection<PageInfo> allArticleNodes = xResolver.resolveNodes("/articles");
        Collection<PageInfo> allArticle2016Nodes = xResolver.resolveNodes(articlesDirectory, "/articles");

        PageInfo page = xResolver.resolveNode(PageInfo.class, articlesDirectory, "2016/index");
        page.setDescription("new description");

        SiteStructureRepository xRepository = null; //
        xRepository.updateNode(articlesDirectory, page);

        PageInfo newPage = new PageInfo();
        xRepository.createNode(articlesDirectory, newPage);

        ContentPage contentPage = cRepository.retrieve(page.getPath(), page.getName());

        cRepository.update(page.getPath(), contentPage);
    }
}