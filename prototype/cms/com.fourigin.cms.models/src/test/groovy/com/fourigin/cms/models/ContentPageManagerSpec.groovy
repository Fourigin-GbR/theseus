package com.fourigin.cms.models

import com.fourigin.cms.models.content.ContentPage
import com.fourigin.cms.models.content.ContentPageManager
import com.fourigin.cms.models.content.UnresolvableContentPathException
import com.fourigin.cms.models.content.elements.ContentGroup
import com.fourigin.cms.models.content.elements.TextContentElement
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(ContentPageManager)
class ContentPageManagerSpec extends Specification {

    @Unroll
    'resolve "#path" works as expected'(){
        given:
        ContentPage page = new ContentPage.Builder()
                .id("page-1")
                .metaData(null)
                .dataSourceContents(null)
                .content(Arrays.asList(
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-1-1").content("Text number 1").build(),
                        new TextContentElement.Builder().name("text-1-2").content("Text number 2").build(),
                        new TextContentElement.Builder().name("text-1-3").content("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-2-1").content("Another text number 1").build(),
                        new TextContentElement.Builder().name("text-2-2").content("Another text number 2").build(),
                        new TextContentElement.Builder().name("text-2-3").content("Another text number 3").build()
                ).build()
        )).build()

        expect:
        result == ContentPageManager.resolve(page, path).name

        where:
        path                            | result
        "/"                             | '' // root
        "/group-1"                      | 'group-1'
        "/group-1/text-1-1"             | 'text-1-1'
        "group-1"                       | 'group-1'
        "//group-1"                     | 'group-1'
        "/group-1/"                     | 'group-1'
        "/group-1//"                    | 'group-1'
    }

    def 'resolve of not existing element throws UnresolvableContentPathException'(){
        given:
        ContentPage page = new ContentPage.Builder()
                .id("page-1")
                .metaData(null)
                .dataSourceContents(null)
                .content(Arrays.asList(
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-1-1").content("Text number 1").build(),
                        new TextContentElement.Builder().name("text-1-2").content("Text number 2").build(),
                        new TextContentElement.Builder().name("text-1-3").content("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-2-1").content("Another text number 1").build(),
                        new TextContentElement.Builder().name("text-2-2").content("Another text number 2").build(),
                        new TextContentElement.Builder().name("text-2-3").content("Another text number 3").build()
                ).build()
        )).build()

        when:
        String path = "/group-1/text-1-1/text-2-1"
        ContentPageManager.resolve(page, path)

        then:
        UnresolvableContentPathException ex = thrown()
        ex.message.contains('Reached the end of the element hierarchy at')
    }

    def 'resolve of not existing group throws UnresolvableContentPathException'(){
        given:
        ContentPage page = new ContentPage.Builder()
                .id("page-1")
                .metaData(null)
                .dataSourceContents(null)
                .content(Arrays.asList(
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-1-1").content("Text number 1").build(),
                        new TextContentElement.Builder().name("text-1-2").content("Text number 2").build(),
                        new TextContentElement.Builder().name("text-1-3").content("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().name("group-1").elements(
                        new TextContentElement.Builder().name("text-2-1").content("Another text number 1").build(),
                        new TextContentElement.Builder().name("text-2-2").content("Another text number 2").build(),
                        new TextContentElement.Builder().name("text-2-3").content("Another text number 3").build()
                ).build()
        )).build()

        when:
        String path = "/group-3"
        ContentPageManager.resolve(page, path)

        then:
        UnresolvableContentPathException ex = thrown()
        ex.message.contains('No element found for part')
    }
}
