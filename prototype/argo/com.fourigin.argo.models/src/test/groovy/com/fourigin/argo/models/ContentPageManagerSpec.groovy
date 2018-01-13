package com.fourigin.argo.models

import com.fourigin.argo.models.content.ContentPage
import com.fourigin.argo.models.content.ContentPageManager
import com.fourigin.argo.models.content.UnresolvableContentPathException
import com.fourigin.argo.models.content.elements.ContentGroup
import com.fourigin.argo.models.content.elements.TextContentElement
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(ContentPageManager)
class ContentPageManagerSpec extends Specification {

    @Unroll
    'resolve "#path" works as expected'(){
        given:
        ContentPage page = new ContentPage.Builder()
                .withId("page-1")
                .withMetaData(null)
                .withDataSourceContents(null)
                .withContent(Arrays.asList(
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-1-1").withContent("Text number 1").build(),
                        new TextContentElement.Builder().withName("text-1-2").withContent("Text number 2").build(),
                        new TextContentElement.Builder().withName("text-1-3").withContent("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-2-1").withContent("Another text number 1").build(),
                        new TextContentElement.Builder().withName("text-2-2").withContent("Another text number 2").build(),
                        new TextContentElement.Builder().withName("text-2-3").withContent("Another text number 3").build()
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
                .withId("page-1")
                .withMetaData(null)
                .withDataSourceContents(null)
                .withContent(Arrays.asList(
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-1-1").withContent("Text number 1").build(),
                        new TextContentElement.Builder().withName("text-1-2").withContent("Text number 2").build(),
                        new TextContentElement.Builder().withName("text-1-3").withContent("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-2-1").withContent("Another text number 1").build(),
                        new TextContentElement.Builder().withName("text-2-2").withContent("Another text number 2").build(),
                        new TextContentElement.Builder().withName("text-2-3").withContent("Another text number 3").build()
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
                .withId("page-1")
                .withMetaData(null)
                .withDataSourceContents(null)
                .withContent(Arrays.asList(
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-1-1").withContent("Text number 1").build(),
                        new TextContentElement.Builder().withName("text-1-2").withContent("Text number 2").build(),
                        new TextContentElement.Builder().withName("text-1-3").withContent("Text number 3").build()
                ).build(),
                new ContentGroup.Builder().withName("group-1").withElements(
                        new TextContentElement.Builder().withName("text-2-1").withContent("Another text number 1").build(),
                        new TextContentElement.Builder().withName("text-2-2").withContent("Another text number 2").build(),
                        new TextContentElement.Builder().withName("text-2-3").withContent("Another text number 3").build()
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
