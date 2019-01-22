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
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentResolver;
import com.fourigin.argo.repository.strategies.NonRecursiveSiteNodeTraversingStrategy;
import com.fourigin.argo.repository.strategies.PatternMatchingSiteNodeTraversingStrategy;
import com.fourigin.argo.repository.strategies.TraversingStrategy;
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

import static com.fourigin.argo.compiler.datasource.DataSourcesResolver.CTX_BASE;

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

//        String customer = (String) context.get(CTX_CUSTOMER);
        String base = (String) context.get(CTX_BASE);

        String path = query.getPath();

        Map<String, String> revisions = id.getRevisions();
        if (revisions == null) {
            revisions = new HashMap<>();
        }

        List<ContentElement> result = new ArrayList<>();

        String nodePattern = query.getNodePattern();

        TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> traversingStrategy;

        if (nodePattern == null) {
            if (query.isNonRecursive()) {
                traversingStrategy = new NonRecursiveSiteNodeTraversingStrategy();
                if (logger.isDebugEnabled()) logger.debug("Resolving nodes with non-recursive traversing strategy");
            } else {
                traversingStrategy = contentResolver.getDefaultTraversingStrategy();
                if (logger.isDebugEnabled()) logger.debug("Resolving nodes with default traversing strategy");
            }
        } else {
            traversingStrategy = new PatternMatchingSiteNodeTraversingStrategy(nodePattern, !query.isNonRecursive());
            if (logger.isDebugEnabled())
                logger.debug("Resolving nodes with pattern-matching traversing strategy for pattern '{}' (non-recursive: {})", nodePattern, query.isNonRecursive());
        }

        Collection<? extends SiteNodeInfo> infos = contentResolver.resolveNodeInfos(path, traversingStrategy);
        if (logger.isDebugEnabled()) logger.debug("Resolved {} nodes", infos.size());

        if (infos != null && !infos.isEmpty()) {
            for (SiteNodeInfo info : infos) {
                if (query.isIgnoreOwnerPage() && info.equals(ownerPage)) {
                    continue;
                }

                if (PageInfo.class.isAssignableFrom(info.getClass())) {
                    PageInfo pageInfo = (PageInfo) info;

                    PageState state = contentResolver.resolvePageState(pageInfo);
                    revisions.put(pageInfo.getReference(), state.getRevision());

                    TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                        .withName("display-name");
                    setContextSpecificValues(info.getDisplayName(), textBuilder);

                    if (query.isVerbose()) {
                        CompileState compileState = state.getCompileState();
                        TemplateReference templateReference = pageInfo.getTemplateReference();
                        PageInfo.ContentPageReference contentReference = pageInfo.getContentPageReference();

                        textBuilder
                            .withAttribute("info.description", info.getDescription())
                            .withAttribute("info.state.staged", String.valueOf(state.isStaged()))
//                            .withAttribute("info.state.checksum", String.valueOf(state.getChecksum()))
                            .withAttribute("info.state.revision", state.getRevision());

                        setContextSpecificAttributeValues(info.getLocalizedName(), "info.localizedName", textBuilder);

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
//                                .withAttribute("info.state.compileState.checksum", compileState.getChecksum())
                                .withAttribute("info.state.compileState.compiled", String.valueOf(compileState.isCompiled()))
//                                .withAttribute("info.state.compileState.timestamp", String.valueOf(compileState.getTimestamp()))
                                .withAttribute("info.state.compileState.message", compileState.getMessage());
                        }
                    }

                    LinkElement.Builder linkBuilder = new LinkElement.Builder()
                        .withTarget(pageInfo.getReference())
                        .withName(info.getName())
                        .withElement(textBuilder.build());

                    List<String> contentReferences = query.getContentReferences();
                    if (contentReferences != null && !contentReferences.isEmpty()) {
                        ContentPage contentPage = contentResolver.retrieve(pageInfo);

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
                } else if (DirectoryInfo.class.isAssignableFrom(info.getClass())) {
                    TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                        .withName("display-name");
                    setContextSpecificValues(info.getDisplayName(), textBuilder);

                    if (query.isVerbose()) {
                        textBuilder
                            .withAttribute("info.description", info.getDescription());

                        setContextSpecificAttributeValues(info.getLocalizedName(), "info.localizedName", textBuilder);
                    }

                    LinkElement.Builder linkBuilder = new LinkElement.Builder()
                        .withTarget(info.getReference())
                        .withName(info.getName())
                        .withElement(textBuilder.build());

                    result.add(linkBuilder.build());
                } else {
                    throw new IllegalStateException("Unsupported node type '" + info.getClass().getName() + "'!");
                }
            }
        }

        id.setRevisions(revisions);

        return result;
    }

    private void setContextSpecificValues(Map<String, String> contextSpecificValues, TextContentElement.Builder textBuilder) {
        if (contextSpecificValues != null) {
            for (Map.Entry<String, String> entry : contextSpecificValues.entrySet()) {
                String base = entry.getKey();
                if ("".equals(base)) {
                    textBuilder.withContent(entry.getValue());
                } else {
                    textBuilder.withContextSpecificContent(base, entry.getValue());
                }
            }
        }
    }

    private void setContextSpecificAttributeValues(Map<String, String> contextSpecificValues, String attributePrefix, TextContentElement.Builder textBuilder) {
        if (contextSpecificValues != null) {
            for (Map.Entry<String, String> entry : contextSpecificValues.entrySet()) {
                String base = entry.getKey();
                String attrName = "".equals(base) ? attributePrefix : attributePrefix + '.' + base;
                textBuilder.withAttribute(attrName, entry.getValue());
            }
        }
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
