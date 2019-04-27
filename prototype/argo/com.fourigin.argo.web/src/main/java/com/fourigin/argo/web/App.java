package com.fourigin.argo.web;

import com.fourigin.argo.assets.processor.AssetsContentPageProcessor;
import com.fourigin.argo.assets.repository.AssetResolver;
import com.fourigin.argo.compiler.DefaultPageCompilerFactory;
import com.fourigin.argo.compiler.datasource.CommonContentDataSource;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.compiler.processor.ContentPageProcessor;
import com.fourigin.argo.config.ProjectsConfiguration;
import com.fourigin.argo.controller.assets.ThumbnailDimensions;
import com.fourigin.argo.controller.assets.ThumbnailResolver;
import com.fourigin.argo.forms.config.ProjectSpecificConfiguration;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.RuntimeConfigurationResolverFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.requests.CmsRequestAggregationResolver;
import com.fourigin.argo.scheduling.AutowiringSpringBeanJobFactory;
import com.fourigin.argo.scheduling.CompileJob;
import com.fourigin.argo.strategies.CmsInternalLinkResolutionStrategy;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.strategies.DefaultFilenameStrategy;
import com.fourigin.argo.strategies.DocumentRootResolverStrategy;
import com.fourigin.argo.strategies.FileCompilerOutputStrategy;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.strategies.MappingDocumentRootResolverStrategy;
import com.fourigin.argo.strategies.StagingInternalLinkResolutionStrategy;
import com.fourigin.argo.template.engine.ArgoTemplateEngine;
import com.fourigin.argo.template.engine.ArgoTemplateEngineFactory;
import com.fourigin.argo.template.engine.DefaultArgoTemplateEngineFactory;
import com.fourigin.argo.template.engine.InternalTemplateEngineFactory;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.ThymeleafArgoTemplateEngine;
import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.awt.Dimension;
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
    "com.fourigin.argo.scheduling",
    "com.fourigin.argo.config"
})
@SpringBootApplication(
    exclude = {
        ThymeleafAutoConfiguration.class
    }
)
@EnableCaching
public class App {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${template.engine.thymeleaf.base}")
    private String templateBasePath;

    @Value("${prepared-content.base}")
    private String preparedContentRoot;

    @Value("${document-root.base}")
    private String documentRootBasePath;

    @Value("${assets.load-balancer-root}")
    private String loadBalancerBasePath;

    @Value("${assets.thumbnails.target}")
    private String thumbnailsDirectory;

    private ContentRepositoryFactory contentRepositoryFactory;

    private RuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory;

    private ArgoTemplateEngineFactory templateEngineFactory;

    private TemplateResolver templateResolver;

    @Autowired
    private ProjectsConfiguration projectsConfiguration;

    private AssetResolver assetResolver;
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter()  // DEFAULT: application.pid
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
        return new MappingDocumentRootResolverStrategy(projectsConfiguration.pathResolver(), documentRootBasePath);
    }

    @Bean
    @Qualifier("STAGE")
    public CompilerOutputStrategy fileCompilerOutputStrategy() {
        FileCompilerOutputStrategy fileCompilerOutputStrategy = new FileCompilerOutputStrategy();

        fileCompilerOutputStrategy.setDocumentRootResolverStrategy(documentRootResolverStrategy());
        fileCompilerOutputStrategy.setFilenameStrategy(filenameStrategy());

        return fileCompilerOutputStrategy;
    }

    // *** TEMPLATE ***

//    private SpringTemplateEngine springTemplateEngine() {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//
//        templateEngine.setTemplateResolver(fileTemplateResolver());
//
//        return templateEngine;
//    }

//    private FileTemplateResolver fileTemplateResolver() {
//        FileTemplateResolver templateResolver = new FileTemplateResolver();
//
//        String prefix = templateBasePath;
//        if (!prefix.endsWith("/")) {
//            prefix += "/";
//        }
//        templateResolver.setPrefix(prefix);
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML");
//        templateResolver.setCacheable(false);
//        templateResolver.setCacheTTLMs(1L);
//        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        return templateResolver;
//    }

    @Bean
    public InternalTemplateEngineFactory internalTemplateEngineFactory(){
        InternalTemplateEngineFactory factory = new InternalTemplateEngineFactory();

        factory.setPathResolver(projectsConfiguration.pathResolver());
        factory.setTemplateBasePath(templateBasePath);

        return factory;
    }

    @Bean
    public ThymeleafArgoTemplateEngine thymeleafTemplateEngine() {
        ThymeleafArgoTemplateEngine thymeleafEngine = new ThymeleafArgoTemplateEngine();
        thymeleafEngine.setInternalTemplateEngineFactory(internalTemplateEngineFactory());
        thymeleafEngine.setInternalLinkResolutionStrategies(internalLinkResolutionStrategies());
        return thymeleafEngine;
    }

    private Map<ProcessingMode, InternalLinkResolutionStrategy> internalLinkResolutionStrategies() {
        Map<ProcessingMode, InternalLinkResolutionStrategy> result = new HashMap<>();

        result.put(ProcessingMode.CMS, new CmsInternalLinkResolutionStrategy());
        result.put(ProcessingMode.STAGE, new StagingInternalLinkResolutionStrategy(contentRepositoryFactory));
        result.put(ProcessingMode.LIVE, new StagingInternalLinkResolutionStrategy(contentRepositoryFactory)); // TODO: verify!

        return result;
    }

    @Bean
    public DefaultArgoTemplateEngineFactory defaultTemplateEngineFactory() {
        DefaultArgoTemplateEngineFactory factory = new DefaultArgoTemplateEngineFactory();

        Map<Type, ArgoTemplateEngine> engines = new HashMap<>();

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
            contentPageProcessors(),
            projectsConfiguration.pathResolver()
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
        assetsContentPageProcessor.setProjectSpecificConfiguration(projectSpecificConfiguration());
        assetsContentPageProcessor.setPathResolver(projectsConfiguration.pathResolver());
        assetsContentPageProcessor.setLoadBalancerBasePath(loadBalancerBasePath);

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
    public void setTemplateEngineFactory(ArgoTemplateEngineFactory templateEngineFactory) {
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
    @ConfigurationProperties(prefix = "project")
    public ProjectSpecificConfiguration projectSpecificConfiguration(){
        return new ProjectSpecificConfiguration();
    }

    //******* QUARTZ
    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(CompileJob.class);
        jobDetailFactory.setDescription("Invoke Sample Job service...");
        jobDetailFactory.setDurability(true);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("project", "ge-web");
        dataMap.put("bases", Arrays.asList("DE", "EN", "RU"));
        jobDetailFactory.setJobDataMap(new JobDataMap(dataMap));

        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(30000); // 30 sek.
//        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatCount(0);
        return trigger;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        schedulerFactory.setJobFactory(springBeanJobFactory(applicationContext));
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
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
