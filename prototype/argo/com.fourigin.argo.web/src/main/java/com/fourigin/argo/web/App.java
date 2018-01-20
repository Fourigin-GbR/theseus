package com.fourigin.argo.web;

import com.fourigin.argo.compiler.DefaultPageCompiler;
import com.fourigin.argo.compiler.DefaultPageCompilerFactory;
import com.fourigin.argo.compiler.PageCompiler;
import com.fourigin.argo.compiler.datasource.DataSourceQueryBuilder;
import com.fourigin.argo.compiler.datasource.DataSourceQueryFactory;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.EmptyDataSourceQuery;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSourceQuery;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.template.engine.DefaultTemplateEngineFactory;
import com.fourigin.argo.template.engine.TemplateEngineFactory;
import com.fourigin.argo.template.engine.ThymeleafTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.fourigin.argo.compile"})
@ComponentScan({"com.fourigin.argo.editors"})
@ComponentScan({"com.fourigin.argo.config"})
@SpringBootApplication(
    exclude = {
        ThymeleafAutoConfiguration.class
    }
)
public class App {

    private static final String APP_NAME = "argo";

    @Value("${template.engine.thymeleaf.base}")
    private String templateBasePath;

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

    // *** TEMPLATE ***

    private TemplateEngine springTemplateEngine(String templateBasePath){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        FileTemplateResolver templateResolver = new FileTemplateResolver();

        String prefix = templateBasePath;
        if(!prefix.endsWith("/")){
            prefix += "/";
        }
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML");

        templateResolver.setCacheable(false);
        templateResolver.setCacheTTLMs(1L);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
    
    @Bean
    public DefaultTemplateEngineFactory templateEngineFactory(){
        DefaultTemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        Map<Type, com.fourigin.argo.template.engine.TemplateEngine> engines = new HashMap<>();

        ThymeleafTemplateEngine thymeleafEngine = new ThymeleafTemplateEngine();
        thymeleafEngine.setThymeleafInternalTemplateEngine(springTemplateEngine(templateBasePath));

        engines.put(Type.THYMELEAF, thymeleafEngine);
        factory.setEngines(engines);

        return factory;
    }

    @Bean
    public TemplateResolver templateResolver(){
        return id -> {
            Set<TemplateVariation> variations = new HashSet<>();

            TemplateVariation variation = new TemplateVariation();
            variation.setId("default");
            variation.setType(Type.THYMELEAF);
            variation.setOutputContentType("text/html");
            variations.add(variation);

            Template template = new Template();
            template.setId(id);
            template.setRevision(null);
            template.setVariations(variations);

            return template;
        };
    }

    // *** COMPILER ***
    private DefaultPageCompiler createPageCompiler(
        String base,
        ContentRepositoryFactory contentRepositoryFactory,
        TemplateEngineFactory templateEngineFactory,
        TemplateResolver templateResolver
    ){
        Map<String, DataSourceQueryBuilder> queryBuilders = new HashMap<>();
        queryBuilders.put(TimestampDataSource.TYPE, new DataSourceQueryBuilder(EmptyDataSourceQuery.class));
        queryBuilders.put(SiteStructureDataSource.TYPE, new DataSourceQueryBuilder(SiteStructureDataSourceQuery.class));
        DataSourceQueryFactory.setBuilders(queryBuilders);

        DefaultPageCompiler compiler = new DefaultPageCompiler();

        compiler.setBase(base);
        compiler.setContentRepository(contentRepositoryFactory.getInstance(base));
        compiler.setTemplateEngineFactory(templateEngineFactory);
        compiler.setTemplateResolver(templateResolver);
        compiler.setDataSourcesResolver(dataSourcesResolver());

        return compiler;
    }

    @Bean
    public DefaultPageCompilerFactory pageCompilerFactory(){
        DefaultPageCompilerFactory factory = new DefaultPageCompilerFactory();

        Map<String, PageCompiler> compilers = new HashMap<>();
        compilers.put("DE", createPageCompiler("DE", contentRepositoryFactory, templateEngineFactory, templateResolver));
        factory.setCompilers(compilers);

        return factory;
    }

    private DataSourcesResolver dataSourcesResolver(){
        DataSourcesResolver resolver = new DataSourcesResolver();

        resolver.setDataSources(Arrays.asList(
            new TimestampDataSource(),
            new SiteStructureDataSource()
        ));

        return resolver;
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
