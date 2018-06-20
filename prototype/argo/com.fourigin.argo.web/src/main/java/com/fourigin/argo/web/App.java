package com.fourigin.argo.web;

import com.fourigin.argo.compiler.DefaultPageCompilerFactory;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepositoryFactory;
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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@ComponentScan({
    "com.fourigin.argo.web",
    "com.fourigin.argo.compile",
    "com.fourigin.argo.editors",
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

    @Value("${document-root.base}")
    private String documentRootBasePath;

    private ContentRepositoryFactory contentRepositoryFactory;

    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private TemplateEngineFactory templateEngineFactory;

    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private TemplateResolver templateResolver;

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
//        return new PlaceholderDocumentRootResolverStrategy(documentRootBasePath);

        // TODO: make configurable!
        Map<String, String> mapping = new HashMap<>();
        mapping.put("DE", documentRootBasePath + "greekestate.de");
        mapping.put("EN", documentRootBasePath + "greekestate.en");
        mapping.put("RU", documentRootBasePath + "greekestate.ru");

        return new MappingDocumentRootResolverStrategy(mapping);
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
        return new DefaultPageCompilerFactory(contentRepositoryFactory, templateEngineFactory, templateResolver, dataSourcesResolver());
    }

    private DataSourcesResolver dataSourcesResolver() {
        DataSourcesResolver resolver = new DataSourcesResolver();

        resolver.setDataSources(Arrays.asList(
            new TimestampDataSource(),
            new SiteStructureDataSource()
        ));

        return resolver;
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
