package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.UnresolvableContentPathException;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.LinkElement;
import com.fourigin.argo.models.content.elements.TextAwareContentElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSource;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.datasource.index.FieldDefinition;
import com.fourigin.argo.models.datasource.index.FieldValue;
import com.fourigin.argo.models.datasource.index.IndexAwareDataSource;
import com.fourigin.argo.models.datasource.index.IndexDefinition;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SiteStructureDataSource implements
    DataSource<SiteStructureDataSourceQuery>,
    IndexAwareDataSource<SiteStructureDataSourceQuery>,
    ContentResolverAwareDataSource {

    public static final String TYPE = "SITE";

    private final Logger logger = LoggerFactory.getLogger(SiteStructureDataSource.class);

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
                if (query.isIgnoreOwnerPage() && info.equals(ownerPage)) {
                    continue;
                }

                PageState state = contentResolver.resolvePageState(info);
                revisions.put(info.getReference(), state.getRevision());

                TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                    .withName("display-name")
                    .withContent(info.getDisplayName());

                if (query.isVerbose()) {
                    CompileState compileState = state.getCompileState();
                    TemplateReference templateReference = info.getTemplateReference();
                    PageInfo.ContentPageReference contentReference = info.getContentPageReference();

                    textBuilder
                        .withAttribute("info.description", info.getDescription())
                        .withAttribute("info.localizedName", info.getLocalizedName())
                        .withAttribute("info.state.staged", String.valueOf(state.isStaged()))
                        .withAttribute("info.state.checksum", String.valueOf(state.getChecksum()))
                        .withAttribute("info.state.revision", state.getRevision());

                    if (templateReference != null) {
                        textBuilder
                            .withAttribute("info.template.templateId", templateReference.getTemplateId())
                            .withAttribute("info.template.variationId", templateReference.getVariationId())
                            .withAttribute("info.template.revision", templateReference.getRevision());
                    }

                    if (contentReference != null) {
                        textBuilder
                            .withAttribute("info.content.parent", contentReference.getParentPath())
                            .withAttribute("info.content.id", contentReference.getContentId());
                    }

                    if (compileState != null) {
                        textBuilder
                            .withAttribute("info.state.compileState.checksum", compileState.getChecksum())
                            .withAttribute("info.state.compileState.compiled", String.valueOf(compileState.isCompiled()))
                            .withAttribute("info.state.compileState.timestamp", String.valueOf(compileState.getTimestamp()))
                            .withAttribute("info.state.compileState.message", compileState.getMessage());
                    }
                }

                LinkElement.Builder linkBuilder = new LinkElement.Builder()
                    .withTarget(info.getReference())
                    .withName(info.getName())
                    .withElement(textBuilder.build());

                List<String> contentReferences = query.getContentReferences();
                if (contentReferences != null && !contentReferences.isEmpty()) {
                    ContentPage contentPage = contentResolver.retrieve(info);

                    for (String reference : contentReferences) {
                        ContentElement resolved;
                        try {
                            resolved = ContentPageManager.resolve(contentPage, reference);
                            if (resolved == null) {
                                if (logger.isWarnEnabled())
                                    logger.warn("Unable to resolve content reference {} for {}", reference, info);
                                continue;
                            }
                        } catch (UnresolvableContentPathException ex) {
                            if (logger.isWarnEnabled())
                                logger.warn("Unable to resolve content reference {} for {}", reference, info);
                            continue;
                        }

                        if (logger.isInfoEnabled())
                            logger.info("Adding resolved content element for reference {} from {}", reference, info);
                        linkBuilder.withElement(resolved);
                    }
                }

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
        if (indexDefinition == null) {
            return null;
        }

        String indexName = indexDefinition.getName();
        Map<String, String> categoryDefinitions = indexDefinition.getCategories();
        Set<FieldDefinition> fieldDefinitions = indexDefinition.getFields();
        Set<String> fullTextSearch = indexDefinition.getFullTextSearch();
        Set<String> keywords = indexDefinition.getKeywords();

        if (generatedContent != null && !generatedContent.isEmpty()) {

            List<String> references = new ArrayList<>();

            List<String> targets = new ArrayList<>();

            Map<String, Map<String, List<Integer>>> categories = new HashMap<>();

            List<FieldValue> fields = new ArrayList<>();
            for (FieldDefinition fieldDefinition : fieldDefinitions) {
                FieldValue fieldValue = new FieldValue();
                fieldValue.setName(fieldDefinition.getName());
                fieldValue.setType(fieldDefinition.getType());
                fieldValue.setPath(fieldDefinition.getPath());
                fields.add(fieldValue);
            }

            Map<String, Set<Integer>> searchValues = new HashMap<>();

            int referenceNumber = 0;
            for (ContentElement referenceElement : generatedContent) {
                LinkElement linkElement = (LinkElement) referenceElement;

                // reference
                String reference = linkElement.getName();
                references.add(reference);

                // target
                String target = linkElement.getTarget();
                targets.add(target);

                List<ContentElement> elements = linkElement.getElements();

                for (Map.Entry<String, String> entry : categoryDefinitions.entrySet()) {
                    String categoryName = entry.getKey();
                    String categoryValuePath = entry.getValue();
                    ContentElement categoryValueElement = ContentPageManager.resolveOptional(categoryValuePath, elements);
                    if (categoryValueElement == null) {
                        continue;
                    }

                    String value = getCategoryValue(categoryValueElement);
                    Map<String, List<Integer>> values = categories.get(categoryName);
                    if (values == null) {
                        values = new HashMap<>();
                        categories.put(categoryName, values);
                    }
                    List<Integer> refs = values.get(value);
                    if (refs == null) {
                        refs = new ArrayList<>();
                        values.put(value, refs);
                    }
                    refs.add(referenceNumber);
                }

                for (FieldValue field : fields) {
                    List<String> values = field.getValue();
                    if (values == null) {
                        values = new ArrayList<>();
                        field.setValue(values);
                    }
                    ContentElement fieldElement = ContentPageManager.resolveOptional(field.getPath(), elements);
                    if (fieldElement != null) {
                        String value = getFieldValue(fieldElement);
                        values.add(value);
                    }
                }

                if (keywords != null && !keywords.isEmpty()) {
                    if (logger.isDebugEnabled()) logger.debug("Indexing keywords {}", keywords);

                    StringBuilder builder = new StringBuilder();
                    for (String path : fullTextSearch) {
                        ContentElement textSearchElement = ContentPageManager.resolveOptional(path, elements);
                        if (textSearchElement == null) {
                            continue;
                        }

                        String value = getSearchValue(textSearchElement);
                        if (value != null) {
                            builder.append(value.toLowerCase(Locale.US)).append(';');
                        }
                    }

                    String textForSearch = builder.toString();
                    if (logger.isDebugEnabled()) logger.debug("Collected text: {}", textForSearch);

                    for (String keyword : keywords) {
                        if (textForSearch.contains(keyword)) {
                            if (logger.isDebugEnabled()) logger.debug("Found a match '{}'", keyword);
                            Set<Integer> matches = searchValues.get(keyword);
                            if (matches == null) {
                                matches = new HashSet<>();
                                searchValues.put(keyword, matches);
                            }
                            matches.add(referenceNumber);
                        }
                    }
                }

                referenceNumber++;
            }

            index.setName(indexName);
            index.setReferences(references);
            index.setTargets(targets);
            index.setCategories(categories);
            index.setFields(fields);
            index.setSearchValues(searchValues);
        }

        return index;
    }

    private String getSearchValue(ContentElement element) {
        if (element instanceof TextAwareContentElement) {
            return ((TextAwareContentElement) element).getContent();
        }

        return null;
    }

    //    private String getFieldValue(ContentElement element, FieldType type) {
    private String getFieldValue(ContentElement element) {
        if (element instanceof TextAwareContentElement) {
            return ((TextAwareContentElement) element).getContent();
        }

        return null;
    }

    private String getCategoryValue(ContentElement element) {
        if (element instanceof TextAwareContentElement) {
            return ((TextAwareContentElement) element).getContent();
        }

        return null;
    }
}
