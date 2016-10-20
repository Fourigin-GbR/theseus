package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteDirectory;
import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SitePage;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Playground {
    public static void main(String[] args) {
        SiteStructureResolver xResolver = null; //factory.getInstance("de_DE");

        Map<String, String> siteAttributes = xResolver.resolveSiteAttributes();
        List<SiteNode> rootNodes = xResolver.resolveNode(SiteDirectory.class, "/").getNodes();
        List<SiteNode> articles2016Absolute = xResolver.resolveNode(SiteDirectory.class, "/articles/2016").getNodes();

        SiteDirectory articlesDirectory = xResolver.resolveNode(SiteDirectory.class, "/articles");
        List<SiteNode> articles2016Relative = xResolver.resolveNode(SiteDirectory.class, articlesDirectory, "2016").getNodes();

        Collection<SitePage> allNodes = xResolver.resolveNodes("/");
        Collection<SitePage> allArticleNodes = xResolver.resolveNodes("/articles");
        Collection<SitePage> allArticle2016Nodes = xResolver.resolveNodes(articlesDirectory, "/articles");

        SitePage page = xResolver.resolveNode(SitePage.class, articlesDirectory, "2016/index");
        page.setDescription("new description");

        SiteStructureRepository xRepository = null; //
        xRepository.updateNode(articlesDirectory, page);

        SitePage newPage = new SitePage();
        xRepository.createNode(articlesDirectory, newPage);
    }
}