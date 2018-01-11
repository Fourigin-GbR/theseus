package com.fourigin.argo.web;

import com.fourigin.argo.compiler.DefaultPageCompiler;
import com.fourigin.argo.compiler.DefaultPageCompilerFactory;
import com.fourigin.argo.compiler.PageCompiler;
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
import org.thymeleaf.templateresolver.ITemplateResolver;

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

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
        );
        app.run(args);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        objectMapper.setDateFormat(new ISO8601DateFormat());
//        objectMapper.registerModule(new ContentPageModule());
//        return objectMapper;
//    }

    // *** CONTENT REPOSITORY ***
//    @Bean
//    public ContentRepositoryStubFactory contentRepositoryStubFactory(){
//        Map<String, ContentRepository> repositories = new HashMap<>();
//
//        Map<String, String> deSiteAttributes = new HashMap<>();
//        Map<String, SiteNodeInfo> deInfos = new HashMap<>();
//        Map<PageInfo, ContentPage> dePages = new HashMap<>();
//
//        DirectoryInfo dir = stubDirectory("dir-1", "/", null);
//
//        List<SiteNodeInfo> pages = new ArrayList<>();
//
//        deInfos.put("/", dir);
//
//        for(int i=1; i<=3; i++){
//            PageInfo page = stubPage("page-" + i, "/dir-1/", dir);
//
//            deInfos.put("/dir-1/page-" + i, page);
//
//            pages.add(page);
//
//            dePages.put(page, new ContentPage.Builder()
//                .id("page-" + i)
//                .metaData(null)
//                .dataSourceContents(null)
//                .content(Arrays.asList(
//                    new ContentGroup.Builder().name("group-1-from-" + i).elements(
//                        new TextContentElement.Builder().name("text-1-1").content("Text number 1 (" + i + ")").build(),
//                        new TextContentElement.Builder().name("text-1-2").content("Text number 2 (" + i + ")").build(),
//                        new TextContentElement.Builder().name("text-1-3").content("Text number 3 (" + i + ")").build()
//                    ).build(),
//                    new ContentGroup.Builder().name("group-2-from-" + i).elements(
//                        new TextContentElement.Builder().name("text-2-1").content("Another text number 1 (" + i + ")").build(),
//                        new TextContentElement.Builder().name("text-2-2").content("Another text number 2 (" + i + ")").build(),
//                        new TextContentElement.Builder().name("text-2-3").content("Another text number 3 (" + i + ")").build()
//                    ).build()
//                )).build()
//            );
//        }
//
//        dir.setNodes(pages);
//
//        repositories.put("DE", new ContentRepositoryStub(deSiteAttributes, deInfos, dePages));
//
//        return new ContentRepositoryStubFactory(repositories);
//    }
//
//    private DirectoryInfo stubDirectory(String name, String path, SiteNodeContainerInfo parent){
//        DirectoryInfo dir = new DirectoryInfo();
//
//        dir.setName(name);
//        dir.setDisplayName("display-name: " + name);
//        dir.setLocalizedName("localized-name: " + name);
//        dir.setDescription("description: " + name);
//        dir.setPath(path);
//        dir.setParent(parent);
//
//        return dir;
//    }
//
//    private PageInfo stubPage(String name, String path, SiteNodeContainerInfo parent){
//        TemplateReference templateReference = new TemplateReference();
//        templateReference.setTemplateId("template-for-" + name);
//        templateReference.setVariationId("default");
//
//        PageInfo page = new PageInfo();
//
//        page.setName(name);
//        page.setDisplayName("display-name: " + name);
//        page.setLocalizedName("localized-name: " + name);
//        page.setDescription("description: " + name);
//        page.setTemplateReference(templateReference);
//        page.setPath(path);
//        page.setParent(parent);
//
//        return page;
//    }

    // *** TEMPLATE ***
    @Bean
    public ITemplateResolver springTemplateResolver(){
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

    private TemplateEngine springTemplateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(springTemplateResolver());

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
    public ThymeleafTemplateEngine thymeleafTemplateEngine(){
        ThymeleafTemplateEngine thymeleafEngine = new ThymeleafTemplateEngine();
        thymeleafEngine.setThymeleafInternalTemplateEngine(springTemplateEngine());
        return thymeleafEngine;
    }

    @Bean
    public DefaultTemplateEngineFactory templateEngineFactory(){
        DefaultTemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        Map<Type, com.fourigin.argo.template.engine.TemplateEngine> engines = new HashMap<>();
        engines.put(Type.THYMELEAF, thymeleafTemplateEngine());
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

        compiler.setBase(base);
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
