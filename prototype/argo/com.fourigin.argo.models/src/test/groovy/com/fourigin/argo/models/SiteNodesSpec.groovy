package com.fourigin.argo.models

import com.fourigin.argo.models.structure.nodes.DirectoryInfo
import com.fourigin.argo.models.structure.nodes.PageInfo
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo
import com.fourigin.argo.models.structure.nodes.SiteNodes
import com.fourigin.argo.models.template.TemplateReference
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SiteNodesSpec extends Specification {

    @Shared
    TemplateReference dummyTemplateReference = new TemplateReference(
            revision: '1',
            templateId: 'some-template',
            variationId: 'default'
    )

    @Shared
    SiteNodeContainerInfo root = new DirectoryInfo(
            path: '/'
    )

    @Shared
    PageInfo page_home = new PageInfo(
            path: '/',
            name: 'home',
            parent: root,
            templateReference: dummyTemplateReference
    )

    @Shared
    DirectoryInfo dir_regions = new DirectoryInfo(
            path: '/',
            name: 'regions',
            parent: root
    )

    @Shared
    PageInfo page_athen = new PageInfo(
            path: '/regions',
            name: 'athen',
            parent: dir_regions,
            templateReference: dummyTemplateReference
    )

    @Shared
    DirectoryInfo dir_objects = new DirectoryInfo(
            path: '/',
            name: 'objects',
            parent: root
    )

    @Shared
    DirectoryInfo dir_rent = new DirectoryInfo(
            path: '/objects',
            name: 'rent',
            parent: dir_objects
    )

    @Shared
    PageInfo page_rent_1 = new PageInfo(
            path: '/objects/rent',
            name: '1',
            parent: dir_rent,
            templateReference: dummyTemplateReference
    )

    @Unroll
    'getDefaultTarget() for #node works as expected'() {
        setup:

        root.nodes = [
                page_home,
                dir_regions,
                dir_objects
        ]

        dir_regions.nodes = [
                page_athen
        ]

        dir_objects.nodes = [
                dir_rent
        ]

        dir_rent.nodes = [
                page_rent_1
        ]

        when:
        def targetNodePath = SiteNodes.getDefaultTarget(node)

        then:
        targetNodePath == expectedResult

        where:
        node        | expectedResult
        root        | '/home'
        page_home   | '/home'
        dir_regions | '/regions/athen'
        page_athen  | '/regions/athen'
        dir_rent    | '/objects/rent/1'
        page_rent_1 | '/objects/rent/1'
    }
}
