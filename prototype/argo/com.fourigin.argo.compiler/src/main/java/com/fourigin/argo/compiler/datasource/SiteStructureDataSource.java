package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.LinkElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSource;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.datasource.index.FieldDefinition;
import com.fourigin.argo.models.datasource.index.FieldValue;
import com.fourigin.argo.models.datasource.index.IndexAwareDataSource;
import com.fourigin.argo.models.datasource.index.IndexDefinition;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SiteStructureDataSource implements
    DataSource<SiteStructureDataSourceQuery>,
    IndexAwareDataSource<SiteStructureDataSourceQuery>,
    ContentResolverAwareDataSource
{
    public static final String TYPE = "SITE";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<ContentElement> generateContent(PageInfo ownerPage, DataSourceIdentifier id, SiteStructureDataSourceQuery query, Map<String, Object> context) {
        ContentResolver contentResolver = (ContentResolver) context.get(CTX_CONTENT_RESOLVER);

        String path = query.getPath();

        Map<String, String> revisions = id.getRevisions();
        if (revisions == null) {
            revisions = new HashMap<>();
        }

        List<ContentElement> result = new ArrayList<>();

        Collection<PageInfo> infos = contentResolver.resolveInfos(path);
        if (infos != null && !infos.isEmpty()) {
            for (PageInfo info : infos) {
                if(query.isIgnoreOwnerPage()) {
                    if(info.equals(ownerPage)){
                        continue;
                    }
                }

                PageState state = contentResolver.resolvePageState(info);
                revisions.put(info.getReference(), state.getRevision());

                TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                    .withName("display-name")
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

                result.add(linkBuilder.build());
            }
        }

        id.setRevisions(revisions);

        return result;
    }

    @Override
    public DataSourceIndex generateIndex(
        PageInfo ownerPage,
        DataSourceIdentifier id,
        SiteStructureDataSourceQuery query,
        List<ContentElement> generatedContent
    ) {
        DataSourceIndex index = new DataSourceIndex();

        IndexDefinition indexDefinition = id.getIndex();
        if(indexDefinition == null) {
            return null;
        }

        String indexName = indexDefinition.getName();
        Set<String> categoryNames = indexDefinition.getCategories();
        Set<FieldDefinition> fieldDefinitions = indexDefinition.getFields();
        Set<String> fullTextSearch = indexDefinition.getFullTextSearch();

        Map<String, String> categories = new HashMap<>();
        for (String categoryName : categoryNames) {
            categories.put(categoryName, "blah"); // TODO: implement me!
        }

        Set<FieldValue> fields = new HashSet<>();
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            FieldValue fieldValue = new FieldValue();
            fieldValue.setName(fieldDefinition.getName());
            fieldValue.setType(fieldDefinition.getType());
            fieldValue.setValue("blah"); // TODO: implement me!
            fields.add(fieldValue);
        }

        Set<String> searchValues = new HashSet<>();
        for (String textSearch : fullTextSearch) {
            searchValues.add(textSearch + "-blah"); // TODO: implement me!
        }

        index.setName(indexName);
        index.setCategories(categories);
        index.setFields(fields);
        index.setSearchValues(searchValues);
        
        return index;
    }
}
