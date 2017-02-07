package com.fourigin.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fourigin.cms.compiler.DefaultPageCompiler;
import com.fourigin.cms.compiler.DefaultPageCompilerFactory;
import com.fourigin.cms.compiler.PageCompiler;
import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.elements.ContentGroup;
import com.fourigin.cms.models.content.elements.TextContentElement;
import com.fourigin.cms.models.content.elements.mapping.ContentPageModule;
import com.fourigin.cms.models.structure.nodes.DirectoryInfo;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateReference;
import com.fourigin.cms.models.template.TemplateVariation;
import com.fourigin.cms.models.template.Type;
import com.fourigin.cms.repository.ContentRepository;
import com.fourigin.cms.repository.ContentRepositoryFactory;
import com.fourigin.cms.repository.ContentRepositoryStub;
import com.fourigin.cms.repository.ContentRepositoryStubFactory;
import com.fourigin.cms.repository.TemplateResolver;
import com.fourigin.cms.template.engine.DefaultTemplateEngineFactory;
import com.fourigin.cms.template.engine.TemplateEngineFactory;
import com.fourigin.cms.template.engine.ThymeleafTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateModeHandler;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication(
    exclude = {
        ThymeleafAutoConfiguration.class
    }
)
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new ISO8601DateFormat());
        objectMapper.registerModule(new ContentPageModule());
        return objectMapper;
    }

    // *** CONTENT REPOSITORY ***
    @Bean
    public ContentRepositoryStubFactory contentRepositoryStubFactory(){
        Map<String, ContentRepository> repositories = new HashMap<>();

        Map<String, String> deSiteAttributes = new HashMap<>();
        Map<String, SiteNodeInfo> deInfos = new HashMap<>();
        Map<PageInfo, ContentPage> dePages = new HashMap<>();

        DirectoryInfo dir = stubDirectory("dir-1", "/", null);

        List<SiteNodeInfo> pages = new ArrayList<>();

        deInfos.put("/", dir);

        for(int i=1; i<=3; i++){
            PageInfo page = stubPage("page-" + i, "/dir-1/", dir);

            deInfos.put("/dir-1/page-" + i, page);

            pages.add(page);

            dePages.put(page, new ContentPage.Builder()
                .id("page-" + i)
                .metaData(null)
                .dataSourceContents(null)
                .content(Arrays.asList(
                    new ContentGroup.Builder().name("group-1-from-" + i).elements(
                        new TextContentElement.Builder().name("text-1-1").content("Text number 1 (" + i + ")").build(),
                        new TextContentElement.Builder().name("text-1-2").content("Text number 2 (" + i + ")").build(),
                        new TextContentElement.Builder().name("text-1-3").content("Text number 3 (" + i + ")").build()
                    ).build(),
                    new ContentGroup.Builder().name("group-2-from-" + i).elements(
                        new TextContentElement.Builder().name("text-2-1").content("Another text number 1 (" + i + ")").build(),
                        new TextContentElement.Builder().name("text-2-2").content("Another text number 2 (" + i + ")").build(),
                        new TextContentElement.Builder().name("text-2-3").content("Another text number 3 (" + i + ")").build()
                    ).build()
                )).build()
            );
        }

        dir.setNodes(pages);

        repositories.put("DE", new ContentRepositoryStub(deSiteAttributes, deInfos, dePages));

        return new ContentRepositoryStubFactory(repositories);
    }

    private DirectoryInfo stubDirectory(String name, String path, SiteNodeContainerInfo parent){
        DirectoryInfo dir = new DirectoryInfo();

        dir.setName(name);
        dir.setDisplayName("display-name: " + name);
        dir.setLocalizedName("localized-name: " + name);
        dir.setDescription("description: " + name);
        dir.setPath(path);
        dir.setParent(parent);

        return dir;
    }

    private PageInfo stubPage(String name, String path, SiteNodeContainerInfo parent){
        TemplateReference templateReference = new TemplateReference();
        templateReference.setTemplateId("template-for-" + name);
        templateReference.setVariationId("default");

        PageInfo page = new PageInfo();

        page.setName(name);
        page.setDisplayName("display-name: " + name);
        page.setLocalizedName("localized-name: " + name);
        page.setDescription("description: " + name);
        page.setTemplateReference(templateReference);
        page.setPath(path);
        page.setParent(parent);

        return page;
    }

    // *** TEMPLATE ***
    @Bean
    public ITemplateResolver springTemplateResolver(@Value("${template.engine.thymeleaf.base}") String templateBasePath){
        FileTemplateResolver templateResolver = new FileTemplateResolver();

        String prefix = templateBasePath;
        if(!prefix.endsWith("/")){
            prefix += "/";
        }
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML");

        templateResolver.setCacheable(false);

        return templateResolver;
    }

    private TemplateEngine springTemplateEngine(String templateBasePath){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(springTemplateResolver(templateBasePath));

        return templateEngine;
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

    @Bean
    public ThymeleafTemplateEngine thymeleafTemplateEngine(
        @Value("${template.engine.thymeleaf.base}") String templateBasePath
    ){
        ThymeleafTemplateEngine thymeleafEngine = new ThymeleafTemplateEngine();
        thymeleafEngine.setThymeleafInternalTemplateEngine(springTemplateEngine(templateBasePath));
        return thymeleafEngine;
    }

    @Bean
    public DefaultTemplateEngineFactory templateEngineFactory(
        @Value("${template.engine.thymeleaf.base}") String templateBasePath
    ){
        DefaultTemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        Map<Type, com.fourigin.cms.template.engine.TemplateEngine> engines = new HashMap<>();
        engines.put(Type.THYMELEAF, thymeleafTemplateEngine(templateBasePath));
        factory.setEngines(engines);

        return factory;
    }

    // *** COMPILER ***
    private DefaultPageCompiler pageCompiler(
        String base,
        ContentRepositoryFactory contentRepositoryFactory,
        TemplateEngineFactory templateEngineFactory,
        TemplateResolver templateResolver
    ){
        DefaultPageCompiler compiler = new DefaultPageCompiler();

        compiler.setContentRepository(contentRepositoryFactory.getInstance(base));
        compiler.setTemplateEngineFactory(templateEngineFactory);
        compiler.setTemplateResolver(templateResolver);

        return compiler;
    }

    @Bean
    public DefaultPageCompilerFactory pageCompilerFactory(
        @Autowired ContentRepositoryFactory contentRepositoryFactory,
        @Autowired TemplateEngineFactory templateEngineFactory,
        @Autowired TemplateResolver templateResolver
    ){
        DefaultPageCompilerFactory factory = new DefaultPageCompilerFactory();

        Map<String, PageCompiler> compilers = new HashMap<>();
        compilers.put("DE", pageCompiler("DE", contentRepositoryFactory, templateEngineFactory, templateResolver));
        factory.setCompilers(compilers);

        return factory;
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
