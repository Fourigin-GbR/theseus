package com.fourigin.cms.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fourigin.cms.config.ContentPageRepositoryConfiguration
import com.fourigin.cms.models.content.ContentPage
import com.fourigin.cms.models.content.elements.ObjectContentElement
import com.fourigin.cms.models.content.elements.TextContentElement
import com.fourigin.cms.models.content.elements.TextLinkContentElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes=[ContentPageRepositoryConfiguration])
class Spec extends Specification {

    @Autowired
    ObjectMapper objectMapper

    def "test1"() {
        JsonFileContentPageRepository contentPageRepository = new JsonFileContentPageRepository.Builder()
                .objectMapper(objectMapper)
                .contentRoot("/work/content/")
                .build()

        expect:
        contentPageRepository != null
        contentPageRepository.contentRoot == "/work/content/"
        contentPageRepository.objectMapper != null
    }

    def "test2"() {
        ContentPage page = new ContentPage.Builder()
            .id("1")
            .revision("1")
            .content(Arrays.asList(
                new TextContentElement.Builder()
                        .name("text-1")
                        .content("I'm just a text content.")
                        .build(),
                new TextContentElement.Builder()
                        .name("text-2")
                        .content("I'm just an another text content.")
                        .build(),
                new TextLinkContentElement.Builder()
                        .name("textlink-1")
                        .content("I'm a text content with a link.")
                        .url("/link.html")
                        .build(),
                new ObjectContentElement.Builder()
                        .name("image-1")
                        .referenceId("asset17")
                        .mimeType("jpg")
                        .source("/images/asset17.jpg")
                        .build(),
            ))
            .build()

        expect:
        page != null
        page.content != null
    }
}
