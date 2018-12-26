package com.fourigin.argo.web;

import com.fourigin.argo.assets.processor.AssetsContentPageProcessor;
import com.fourigin.argo.assets.repository.AssetResolver;
import com.fourigin.argo.compiler.DefaultPageCompilerFactory;
import com.fourigin.argo.compiler.datasource.CommonContentDataSource;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.compiler.processor.ContentPageProcessor;
import com.fourigin.argo.forms.config.CustomerSpecificConfiguration;
import com.fourigin.argo.controller.assets.ThumbnailDimensions;
import com.fourigin.argo.controller.assets.ThumbnailResolver;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.RuntimeConfigurationResolverFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.strategies.DefaultFilenameStrategy;
import com.fourigin.argo.strategies.DocumentRootResolverStrategy;
import com.fourigin.argo.strategies.FileCompilerOutputStrategy;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.strategies.MappingDocumentRootResolverStrategy;
import com.fourigin.argo.template.engine.DefaultTemplateEngineFactory;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.TemplateEngine;
import com.fourigin.argo.template.engine.TemplateEngineFactory;
import com.fourigin.argo.template.engine.ThymeleafTemplateEngine;
import com.fourigin.argo.template.engine.strategies.CmsInternalLinkResolutionStrategy;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import com.fourigin.argo.template.engine.strategies.StagingInternalLinkResolutionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.awt.Dimension;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@ComponentScan({
    "com.fourigin.argo.web",
    "com.fourigin.argo.controller",
    "com.fourigin.argo.config"
})
@SpringBootApplication(
    exclude = {
        ThymeleafAutoConfiguration.class
    }
)
@EnableCaching
public class App {

    private static final String APP_NAME = "argo";

    @Value("${template.engine.thymeleaf.base}")
    private String templateBasePath;

    @Value("${prepared-content.base}")
    private String preparedContentRoot;

    @Value("${assets.thumbnails.target}")
    private String thumbnailsDirectory;

    private ContentRepositoryFactory contentRepositoryFactory;

    private RuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory;

    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private TemplateEngineFactory templateEngineFactory;

    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private TemplateResolver templateResolver;

    private AssetResolver assetResolver;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
        );
        app.run(args);
    }

    // *** STRATEGIES ***

    @Bean
    public FilenameStrategy filenameStrategy() {
        return new DefaultFilenameStrategy();
    }

    @Bean
    public DocumentRootResolverStrategy documentRootResolverStrategy() {
        return new MappingDocumentRootResolverStrategy(customerSpecificConfiguration());
    }

    @Bean
    public CompilerOutputStrategy fileCompilerOutputStrategy() {
        FileCompilerOutputStrategy fileCompilerOutputStrategy = new FileCompilerOutputStrategy();

        fileCompilerOutputStrategy.setDocumentRootResolverStrategy(documentRootResolverStrategy());
        fileCompilerOutputStrategy.setFilenameStrategy(filenameStrategy());

        return fileCompilerOutputStrategy;
    }

    // *** TEMPLATE ***

    private SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(fileTemplateResolver());

        return templateEngine;
    }

    @SuppressWarnings("Duplicates")
    private FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        String prefix = templateBasePath;
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        templateResolver.setCacheTTLMs(1L);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return templateResolver;
    }

    @Bean
    public ThymeleafTemplateEngine thymeleafTemplateEngine() {
        ThymeleafTemplateEngine thymeleafEngine = new ThymeleafTemplateEngine();
        thymeleafEngine.setThymeleafInternalTemplateEngine(springTemplateEngine());
        thymeleafEngine.setInternalLinkResolutionStrategies(internalLinkResolutionStrategies());
        return thymeleafEngine;
    }

    private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies() {
        Map<ProcessingMode, InternalLinkResolutionStrategy> result = new HashMap<>();

        result.put(ProcessingMode.CMS, new CmsInternalLinkResolutionStrategy());
        result.put(ProcessingMode.STAGE, new StagingInternalLinkResolutionStrategy());
        result.put(ProcessingMode.LIVE, new StagingInternalLinkResolutionStrategy()); // TODO: verify!

        return result;
    }

    @Bean
    public DefaultTemplateEngineFactory templateEngineFactory() {
        DefaultTemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        Map<Type, TemplateEngine> engines = new HashMap<>();

        engines.put(Type.THYMELEAF, thymeleafTemplateEngine());

        factory.setEngines(engines);

        return factory;
    }

    // *** COMPILER ***
    @Bean
    public DefaultPageCompilerFactory pageCompilerFactory() {
        return new DefaultPageCompilerFactory(
            contentRepositoryFactory,
            templateEngineFactory,
            templateResolver,
            dataSourcesResolver(),
            runtimeConfigurationResolverFactory,
            preparedContentRoot,
            contentPageProcessors()
        );
    }

    private DataSourcesResolver dataSourcesResolver() {
        DataSourcesResolver resolver = new DataSourcesResolver();

        resolver.setDataSources(Arrays.asList(
            new TimestampDataSource(),
            new SiteStructureDataSource(),
            new CommonContentDataSource()
        ));

        return resolver;
    }

    @Bean
    public List<ContentPageProcessor> contentPageProcessors(){
        AssetsContentPageProcessor assetsContentPageProcessor = new AssetsContentPageProcessor();
        assetsContentPageProcessor.setAssetResolver(assetResolver);
        assetsContentPageProcessor.setCustomerSpecificConfiguration(customerSpecificConfiguration());
//        assetsContentPageProcessor.setAssetsDomain(assetsDomain);
//        assetsContentPageProcessor.setLoadBalancerDocumentRoots(loadBalancerDocumentRoots);

        return Collections.singletonList(
            assetsContentPageProcessor
        );
    }

    @Bean
    public ThumbnailResolver thumbnailResolver(){
        ThumbnailResolver thumbnailResolver = new ThumbnailResolver();

        thumbnailResolver.setAssetResolver(assetResolver);
        thumbnailResolver.setTargetDirectory(thumbnailsDirectory);
        thumbnailResolver.setUnsupportedMimeTypesIconDirectory(thumbnailsDirectory + "/unsupported-mime-type-icons");

        return thumbnailResolver;
    }

    @Bean
    public ThumbnailDimensions thumbnailDimensions(){
        ThumbnailDimensions dimensions = new ThumbnailDimensions();

        dimensions.put("big", new Dimension(150, 150));
        dimensions.put("small", new Dimension(50, 50));

        return dimensions;
    }

    @Bean
    public CmsRequestAggregationResolver cmsRequestAggregationResolver() {
        return new CmsRequestAggregationResolver(contentRepositoryFactory, templateResolver);
    }

    @Autowired
    public void setContentRepositoryFactory(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Autowired
    public void setTemplateEngineFactory(TemplateEngineFactory templateEngineFactory) {
        this.templateEngineFactory = templateEngineFactory;
    }

    @Autowired
    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    @Autowired
    public void setRuntimeConfigurationResolverFactory(RuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory) {
        this.runtimeConfigurationResolverFactory = runtimeConfigurationResolverFactory;
    }

    @Autowired
    public void setAssetResolver(AssetResolver assetResolver) {
        this.assetResolver = assetResolver;
    }

    @Bean
    @ConfigurationProperties(prefix = "customer")
    public CustomerSpecificConfiguration customerSpecificConfiguration(){
        return new CustomerSpecificConfiguration();
    }

    /*
        <bean id="processEngineConfiguration" class="org.activiti.spring.SpringProcessEngineConfiguration">
            ...
        </bean>

        <bean id="processEngine" class="org.activiti.spring.ProcessEngineFactoryBean">
          <property name="processEngineConfiguration" ref="processEngineConfiguration" />
        </bean>
     */

//    @Bean
//    public ProcessEngine processEngine() throws Exception {
//        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
//        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
//        return factoryBean.getObject();
//    }
//
//    private SpringProcessEngineConfiguration processEngineConfiguration() {
//        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
//        return configuration;
//    }

}
