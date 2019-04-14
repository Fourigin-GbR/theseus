package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.UnresolvableContentPathException;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.LinkElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.datasource.DataSource;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodes;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fourigin.argo.compiler.datasource.DataSourcesResolver.CTX_LANGUAGE;

public class CommonContentDataSource implements
    DataSource<CommonContentDataSourceQuery>,
    ContentResolverAwareDataSource {

    public static final String TYPE = "COMMON-CONTENT";

    private final Logger logger = LoggerFactory.getLogger(CommonContentDataSource.class);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<ContentElement> generateContent(
        PageInfo ownerPage,
        DataSourceIdentifier id,
        CommonContentDataSourceQuery query,
        Map<String, Object> context
    ) {
        ContentResolver contentResolver = (ContentResolver) context.get(CTX_CONTENT_RESOLVER);
//        String customer = (String) context.get(CTX_PROJECT);
        String locale = (String) context.get(CTX_LANGUAGE);

        String path = "/common";

        Map<String, String> revisions = id.getRevisions();
        if (revisions == null) {
            revisions = new HashMap<>();
        }

        List<ContentElement> result = new ArrayList<>();

        PageInfo commonInfo = contentResolver.resolveInfo(PageInfo.class, path);
        if (commonInfo != null) {
            PageState state = contentResolver.resolvePageState(commonInfo);
            revisions.put(commonInfo.getReference(), state.getRevision());

            TextContentElement.Builder textBuilder = new TextContentElement.Builder()
                .withName("display-name")
                .withContent(locale, SiteNodes.resolveContent(locale, commonInfo.getDisplayName()));

            if (query.isVerbose()) {
                CompileState compileState = state.getCompileState();
                TemplateReference templateReference = commonInfo.getTemplateReference();
                PageInfo.ContentPageReference contentReference = commonInfo.getContentPageReference();

                textBuilder
                    .withAttribute("info.description", commonInfo.getDescription())
                    .withAttribute("info.localizedName", SiteNodes.resolveContent(locale, commonInfo.getLocalizedName()))
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
                .withTarget(commonInfo.getReference())
                .withName(commonInfo.getName())
                .withElement(textBuilder.build());

            List<String> contentReferences = query.getContentReferences();
            if (contentReferences != null && !contentReferences.isEmpty()) {
                ContentPage contentPage = contentResolver.retrieve(commonInfo);

                for (String reference : contentReferences) {
                    ContentElement resolved;
                    try {
                        resolved = ContentPageManager.resolve(contentPage, reference);
                        if (resolved == null) {
                            if (logger.isWarnEnabled())
                                logger.warn("Unable to resolve content reference {} for {}", reference, commonInfo);
                            continue;
                        }
                    } catch (UnresolvableContentPathException ex) {
                        if (logger.isWarnEnabled())
                            logger.warn("Unable to resolve content reference {} for {}", reference, commonInfo);
                        continue;
                    }

                    if (logger.isInfoEnabled())
                        logger.info("Adding resolved content element for reference {} from {}", reference, commonInfo);
                    linkBuilder.withElement(resolved);
                }
            }

            result.add(linkBuilder.build());
        }


        id.setRevisions(revisions);

        return result;
    }
}
