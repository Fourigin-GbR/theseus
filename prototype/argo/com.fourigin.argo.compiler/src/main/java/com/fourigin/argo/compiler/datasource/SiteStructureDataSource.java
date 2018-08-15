package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.LinkElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SiteStructureDataSource implements DataSource<SiteStructureDataSourceQuery> {
    public static final String TYPE = "SITE";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ContentElement generateContent(ContentResolver contentResolver, DataSourceIdentifier id, SiteStructureDataSourceQuery query) {
        String path = query.getPath();

        ContentGroup.Builder builder = new ContentGroup.Builder()
            .withName("siteStructureElements");

        Map<String, String> revisions = id.getRevisions();
        if (revisions == null) {
            revisions = new HashMap<>();
        }

        Collection<PageInfo> infos = contentResolver.resolveInfos(path);
        if (infos != null && !infos.isEmpty()) {
            for (PageInfo info : infos) {
                PageState state = contentResolver.resolvePageState(info);
                revisions.put(info.getReference(), state.getRevision());

                TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                    .withName("name")
                    .withContent(info.getDisplayName());

                if (query.isVerbose()) {
                    textBuilder
                        .withAttribute("description", info.getDescription())
                        .withAttribute("localizedName", info.getLocalizedName())
                        .withAttribute("compileState", String.valueOf(state.getCompileState()))
                        .withAttribute("contentPageReference", String.valueOf(info.getContentPageReference()))
                        .withAttribute("templateReference", String.valueOf(info.getTemplateReference()));
                }

                LinkElement.Builder linkBuilder = new LinkElement.Builder()
                    .withTarget(info.getReference())
                    .withName(info.getName())
                    .withElement(textBuilder.build());

                builder.withElement(linkBuilder.build());
            }
        }

        id.setRevisions(revisions);

        return builder.build();
    }
}
