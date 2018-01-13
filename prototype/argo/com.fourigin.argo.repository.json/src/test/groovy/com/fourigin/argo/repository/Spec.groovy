package com.fourigin.argo.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fourigin.argo.config.ContentPageRepositoryConfiguration
import com.fourigin.argo.models.content.ContentPage
import com.fourigin.argo.models.content.elements.ObjectContentElement
import com.fourigin.argo.models.content.elements.TextContentElement
import com.fourigin.argo.models.content.elements.TextLinkContentElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes=[ContentPageRepositoryConfiguration])
class Spec extends Specification {

    @Autowired
    ObjectMapper objectMapper

    def "test1"() {
        JsonFileContentPageRepository contentPageRepository = new JsonFileContentPageRepository.Builder()
                .withObjectMapper(objectMapper)
                .withContentRoot("/work/content/")
                .build()

        expect:
        contentPageRepository != null
        contentPageRepository.contentRoot == "/work/content/"
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
                new TextLinkContentElement.Builder()
                        .withName("textlink-1")
                        .withContent("I'm a text content with a link.")
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
