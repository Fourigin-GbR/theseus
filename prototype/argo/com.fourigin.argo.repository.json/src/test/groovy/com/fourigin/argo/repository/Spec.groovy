package com.fourigin.argo.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fourigin.argo.config.ContentPageRepositoryConfiguration
import com.fourigin.argo.models.content.ContentPage
import com.fourigin.argo.models.content.elements.LinkElement
import com.fourigin.argo.models.content.elements.ObjectContentElement
import com.fourigin.argo.models.content.elements.TextContentElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes=[ContentPageRepositoryConfiguration])
class Spec extends Specification {

    @Autowired
    ObjectMapper objectMapper

    def "test1"() {
        HiddenDirectoryContentRepository contentPageRepository = new HiddenDirectoryContentRepository()
        contentPageRepository.setObjectMapper(objectMapper)
        contentPageRepository.setContentRoot('/work/content')

        expect:
        contentPageRepository != null
        contentPageRepository.contentRoot.absolutePath == '/work/content'
        contentPageRepository.objectMapper != null
    }

    def "test2"() {
        ContentPage page = new ContentPage.Builder()
            .withId("1")
            .withContent(Arrays.asList(
                new TextContentElement.Builder()
                        .withName("text-1")
                        .withContent("I'm just a text content.")
                        .build(),
                new TextContentElement.Builder()
                        .withName("text-2")
                        .withContent("I'm just an another text content.")
                        .build(),
                new LinkElement.Builder()
                        .withName("textlink-1")
                        .withElement(new TextContentElement.Builder()
                            .withName("text-link-1-content")
                            .withContent("I'm a text content with a link.")
                            .build()
                        )
                        .withUrl("/link.html")
                        .build(),
                new ObjectContentElement.Builder()
                        .withName("image-1")
                        .withReferenceId("asset17")
                        .withMimeType("jpg")
                        .withSource("/images/asset17.jpg")
                        .build(),
            ))
            .build()

        expect:
        page != null
        page.content != null
    }
}
