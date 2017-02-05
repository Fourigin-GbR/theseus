package com.fourigin.cms;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.elements.ContentGroup;
import com.fourigin.cms.models.content.elements.TextContentElement;
import com.fourigin.cms.models.structure.nodes.DirectoryInfo;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.repository.ContentRepository;
import com.fourigin.cms.repository.ContentRepositoryStub;
import com.fourigin.cms.repository.ContentRepositoryStubFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }

    @Bean
    public ContentRepositoryStubFactory contentRepositoryStubFactory(){
        Map<String, ContentRepository> repositories = new HashMap<>();

        Map<String, String> deSiteAttributes = new HashMap<>();
        Map<String, SiteNodeInfo> deInfos = new HashMap<>();
        Map<PageInfo, ContentPage> dePages = new HashMap<>();

        DirectoryInfo dir = stubDirectory("dir1", "/", null);

        List<SiteNodeInfo> pages = new ArrayList<>();

        deInfos.put("/", dir);

        for(int i=1; i<=3; i++){
            PageInfo page = stubPage("page-" + i, "/dir1/", dir);

            deInfos.put("/dir1/page" + i, page);

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
                    new ContentGroup.Builder().name("group-1-from-" + i).elements(
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
        PageInfo page = new PageInfo();

        page.setName(name);
        page.setDisplayName("display-name: " + name);
        page.setLocalizedName("localized-name: " + name);
        page.setDescription("description: " + name);
        page.setPath(path);
        page.setParent(parent);

        return page;
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
