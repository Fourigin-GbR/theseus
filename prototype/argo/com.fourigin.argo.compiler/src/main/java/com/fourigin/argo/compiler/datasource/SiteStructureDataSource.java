package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentResolver;

import java.util.Collection;

public class SiteStructureDataSource implements DataSource<SiteStructureDataSourceQuery> {
    public static final String TYPE = "SITE";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ContentElement generateContent(ContentResolver contentResolver, SiteStructureDataSourceQuery query) {
        String path = query.getPath();

        ContentGroup.Builder builder = new ContentGroup.Builder()
            .withName("siteStructureElements");

        Collection<PageInfo> infos = contentResolver.resolveInfos(path);
        if(infos != null && !infos.isEmpty()){
            for (PageInfo info : infos) {
                builder.withElement(new TextContentElement.Builder()
                    .withName(info.getName())
                    .withContent(info.getDisplayName())
                    .withAttribute("path", info.getPath())
                    .withAttribute("description", info.getDescription())
                    .withAttribute("localizedName", info.getLocalizedName())
                    .withAttribute("reference", info.getReference())
                    .withAttribute("compileState", String.valueOf(info.getCompileState()))
                    .withAttribute("contentPageReference", String.valueOf(info.getContentPageReference()))
                    .withAttribute("templateReference", String.valueOf(info.getTemplateReference()))
                    .build());
            }
        }

        return builder.build();
    }
}
