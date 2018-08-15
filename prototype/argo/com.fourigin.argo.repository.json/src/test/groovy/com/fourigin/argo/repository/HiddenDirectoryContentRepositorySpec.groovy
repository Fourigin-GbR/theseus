package com.fourigin.argo.repository

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class HiddenDirectoryContentRepositorySpec extends Specification {

    @Shared
    String rootPath = new File('src/test/resources/content-root').absolutePath

    @Unroll
    'getHiddenDirectory(#path) returns #expectedResult'() {
        when:
        HiddenDirectoryContentRepository repository = new HiddenDirectoryContentRepository()
        repository.setContentRoot(rootPath)

        def hidden = repository.getHiddenDirectory(path)

        then:
        hidden.absolutePath == expectedResult

        where:
        path                   | expectedResult
        '/'                    | rootPath + '/.cms'
        '/home'                | rootPath + '/.cms'
        '/regions'             | rootPath + '/regions/.cms'
        '/regions/athen'       | rootPath + '/regions/.cms'
        '/objects/rent'        | rootPath + '/objects/rent/.cms'
        '/objects/rent/1'      | rootPath + '/objects/rent/.cms'
    }

    def 'getHiddenDirectory returns null for a non-existing path'() {
        setup:
        HiddenDirectoryContentRepository repository = new HiddenDirectoryContentRepository()
        repository.setContentRoot(rootPath)

        when:
        def hidden = repository.getHiddenDirectory("/a-non-existing-path")

        then:
        hidden == null
//        NullPointerException ex = thrown()
//        ex.message == 'availability must not be null!'
    }


}
