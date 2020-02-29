package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.template.engine.api.Argo;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.argo.template.engine.utilities.ContentPageAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.PageInfoAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ProcessingModeAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ProjectAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.RootSiteNodeAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.SiteAttributesAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtilityFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ThymeleafArgoTemplateEngine implements ArgoTemplateEngine, PageInfoAwareTemplateEngine, SiteAttributesAwareTemplateEngine {

    private InternalTemplateEngineFactory internalTemplateEngineFactory;

    private Map<String, ThymeleafTemplateUtilityFactory> templateUtilityFactories;

    private String utilitiesPrefix;

    private String project;

    private String language;

    private String path;

    private PageInfo pageInfo;

    private Map<String, String> siteAttributes;

    private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies;

    private static final String DEFAULT_UTILITIES_PREFIX = "util_";

    private final Logger logger = LoggerFactory.getLogger(ThymeleafArgoTemplateEngine.class);

    public ThymeleafArgoTemplateEngine duplicate() {
        ThymeleafArgoTemplateEngine clone = new ThymeleafArgoTemplateEngine();

        clone.setInternalTemplateEngineFactory(internalTemplateEngineFactory);
        clone.setTemplateUtilityFactories(templateUtilityFactories);
        clone.setProject(project);
        clone.setLanguage(language);
        clone.setPath(path);
        clone.setPageInfo(pageInfo);
        clone.setSiteAttributes(siteAttributes);
        clone.setUtilitiesPrefix(utilitiesPrefix);
        clone.setInternalLinkResolutionStrategies(internalLinkResolutionStrategies);

        return clone;
    }

    @Override
    public void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out) {
        TemplateEngine templateEngine = internalTemplateEngineFactory.getInstance(project);
        if (templateEngine == null) {
            throw new IllegalStateException("No thymeleaf internal template engine defined!");
        }

        String templateName = template.getId();
        if (!TemplateVariation.DEFAULT_VARIATION_NAME.equals(templateVariation.getId())) {
            templateName += "#" + templateVariation.getId();
        }
        templateName = templateName.replace('.', '/');
        if (logger.isDebugEnabled()) logger.debug("Template name: {}.", templateName);

        Context context = new Context();
        context.setVariable("argo", new Argo.Builder()
            .withProject(project)
            .withLanguage(language)
            .withPath(path)
            .withContentPage(contentPage)
            .withPageInfo(pageInfo)
            .withProcessingMode(processingMode)
            .withSiteAttributes(siteAttributes)
            .withInternalLinkResolutionStrategies(internalLinkResolutionStrategies)
            .withRootNodeInfo(resolveRoot())
            .build()
        );

        addUtilities(templateUtilityFactories, context, language, processingMode);

        try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            templateEngine.process(templateName, context, writer);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling content page!", ex);
            throw new ContentPageCompilerException("Error occurred while compiling content page!", ex);
        }
    }

    private void addUtilities(Map<String, ThymeleafTemplateUtilityFactory> utilities, Context context, String language, ProcessingMode processingMode) {
        if (utilities != null && !utilities.isEmpty()) {
            String prefix = utilitiesPrefix != null ? utilitiesPrefix : DEFAULT_UTILITIES_PREFIX;

            if (logger.isDebugEnabled()) logger.debug("Adding template utilities (using prefix '{}'):", prefix);
            for (Map.Entry<String, ThymeleafTemplateUtilityFactory> entry : utilities.entrySet()) {
                String utilityName = entry.getKey();
                ThymeleafTemplateUtilityFactory factory = entry.getValue();

                // initialize
                ThymeleafTemplateUtility utility = factory.getInstance();
                Class<? extends ThymeleafTemplateUtility> utilityClass = utility.getClass();
                if (ContentPageAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    ContentPage contentPage = (ContentPage) context.getVariable(CONTENT_PAGE);
                    ContentPageAwareThymeleafTemplateUtility contentPageUtility = (ContentPageAwareThymeleafTemplateUtility) utility;
                    contentPageUtility.setContentPage(contentPage);
                    contentPageUtility.setLanguage(language);
                }
                if (PageInfoAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    PageInfoAwareThymeleafTemplateUtility pageInfoUtility = (PageInfoAwareThymeleafTemplateUtility) utility;
                    pageInfoUtility.setPageInfo(pageInfo);
                }
                if (ProjectAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    ProjectAwareThymeleafTemplateUtility projectAwareUtility = (ProjectAwareThymeleafTemplateUtility) utility;
                    projectAwareUtility.setProject(project);
                }
                if (SiteAttributesAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    SiteAttributesAwareThymeleafTemplateUtility siteAttributesUtility = (SiteAttributesAwareThymeleafTemplateUtility) utility;
                    siteAttributesUtility.setLanguage(language);
                    siteAttributesUtility.setSiteAttributes(siteAttributes);
                }
                if (ProcessingModeAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    ProcessingModeAwareThymeleafTemplateUtility processingModeUtility = (ProcessingModeAwareThymeleafTemplateUtility) utility;
                    processingModeUtility.setProcessingMode(processingMode);
                }
                if (RootSiteNodeAwareThymeleafTemplateUtility.class.isAssignableFrom(utilityClass)) {
                    RootSiteNodeAwareThymeleafTemplateUtility rootNodeUtility = (RootSiteNodeAwareThymeleafTemplateUtility) utility;
                    rootNodeUtility.setRootSiteNode(resolveRoot());
                }

                if (logger.isDebugEnabled())
                    logger.debug(" - {}, class {}", utilityName, utilityClass.getName());

                Argo argo = (Argo) context.getVariable("argo");
                argo.addCustomUtility(utilityName, utility);
            }
        }
    }

    private SiteNodeContainerInfo resolveRoot() {
        SiteNodeContainerInfo root = pageInfo.getParent();
        if (root == null) {
            return (SiteNodeContainerInfo) pageInfo;
        }

        while (root.getParent() != null) {
            root = root.getParent();
        }

        return root;
    }

    public void setInternalTemplateEngineFactory(InternalTemplateEngineFactory internalTemplateEngineFactory) {
        this.internalTemplateEngineFactory = internalTemplateEngineFactory;
    }

    public void setTemplateUtilityFactories(Map<String, ThymeleafTemplateUtilityFactory> templateUtilityFactories) {
        this.templateUtilityFactories = templateUtilityFactories;
    }

    public void setUtilitiesPrefix(String utilitiesPrefix) {
        this.utilitiesPrefix = utilitiesPrefix;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public void setSiteAttributes(Map<String, String> siteAttributes) {
        this.siteAttributes = siteAttributes;
    }

    public void setInternalLinkResolutionStrategies(Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies) {
        this.internalLinkResolutionStrategies = internalLinkResolutionStrategies;
    }

    @Override
    public String toString() {
        return "ThymeleafArgoTemplateEngine{" +
            "internalTemplateEngineFactory=" + internalTemplateEngineFactory +
            ", templateUtilityFactories=" + templateUtilityFactories +
            ", utilitiesPrefix='" + utilitiesPrefix + '\'' +
            ", project='" + project + '\'' +
            ", language='" + language + '\'' +
            ", path='" + path + '\'' +
            ", pageInfo=" + pageInfo +
            ", siteAttributes=" + siteAttributes +
            ", internalLinkResolutionStrategies=" + internalLinkResolutionStrategies +
            '}';
    }
}
