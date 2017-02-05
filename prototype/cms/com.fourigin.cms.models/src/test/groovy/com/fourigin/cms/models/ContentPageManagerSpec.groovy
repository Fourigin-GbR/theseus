package com.fourigin.cms.models

import com.fourigin.cms.models.content.ContentPage
import com.fourigin.cms.models.content.ContentPageManager
import com.fourigin.cms.models.content.elements.ContentGroup
import com.fourigin.cms.models.content.elements.TextContentElement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(ContentPageManager)
class ContentPageManagerSpec extends Specification {

    @Shared
    TextContentElement text1 = new TextContentElement.Builder().name("text-1-1").content("Text number 1").build()

    @Shared
    ContentGroup group1 = new ContentGroup.Builder().name("group-1").elements(
            text1,
            new TextContentElement.Builder().name("text-1-2").content("Text number 2").build(),
            new TextContentElement.Builder().name("text-1-3").content("Text number 3").build()
    ).build()

    @Shared
    ContentGroup group2 = new ContentGroup.Builder().name("group-1").elements(
            new TextContentElement.Builder().name("text-2-1").content("Another text number 1").build(),
            new TextContentElement.Builder().name("text-2-2").content("Another text number 2").build(),
            new TextContentElement.Builder().name("text-2-3").content("Another text number 3").build()
    ).build()


    @Unroll
    'resolve "#path"'(){
        setup:
        ContentPage page = new ContentPage.Builder()
            .id("page-1")
            .metaData(null)
            .dataSourceContents(null)
            .content(Arrays.asList(group1, group2))
            .build()

        expect:
        result == ContentPageManager.resolve(page, path)

        where:
        path                            | result
        "/"                             | null
        "/group-1"                      | group1
        "/group-1/text-1-1"             | text1
        "/group-1/text-1-1/text-2-1"    | null
        "/group-3"                      | null
        "group-1"                       | group1
        "//group-1"                     | group1
        "/group-1/"                     | group1
        "/group-1//"                    | group1
    }
}
