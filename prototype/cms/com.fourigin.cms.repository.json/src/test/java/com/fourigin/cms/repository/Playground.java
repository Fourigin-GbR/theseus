package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteDirectory;
import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SitePage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Playground {
    public static void main(String[] args) {
        SiteStructureResolver xResolver = null; //factory.getInstance("de_DE");

        Map<String, String> siteAttributes = xResolver.resolveSiteAttributes();
        List<SiteNode> rootNodes = xResolver.resolveNodes("/");
        List<SiteNode> articles2016Absolute = xResolver.resolveNodes("/articles/2016");

        SiteDirectory articlesDirectory = xResolver.resolveNode(SiteDirectory.class, "/articles");
        List<SiteNode> articles2016Relative = xResolver.resolveNodes(articlesDirectory, "2016");

        Iterator<SiteNode> allNodes = xResolver.resolveIterator("/");
        Iterator<SiteNode> allArticleNodes = xResolver.resolveIterator("/articles");
        Iterator<SiteNode> allArticle2016Nodes = xResolver.resolveIterator(articlesDirectory, "/articles");

        SitePage page = xResolver.resolveNode(SitePage.class, articlesDirectory, "2016/index");
        page.setDescription("new description");

        SiteStructureRepository xRepository = null; //
        xRepository.updateNode(articlesDirectory, page);

        SitePage newPage = new SitePage();
        xRepository.createNode(articlesDirectory, newPage);
    }
}