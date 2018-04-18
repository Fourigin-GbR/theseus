package com.fourigin.argo.strategies

import com.fourigin.argo.models.structure.CompileState
import com.fourigin.argo.models.structure.nodes.DirectoryInfo
import com.fourigin.argo.models.structure.nodes.PageInfo
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo
import com.fourigin.argo.models.template.TemplateReference
import spock.lang.Specification
import spock.lang.Unroll

class DefaultFilenameStrategySpec extends Specification {

    static TemplateReference templateReference = new TemplateReference()
    static CompileState compileState = new CompileState()
    static {
        templateReference.setTemplateId("unspecified")
        templateReference.setVariationId("unspecified")
        templateReference.setRevision("unspecified")

        compileState.setChecksum("")
        compileState.setCompiled(false)
        compileState.setMessage("")
        compileState.setTimestamp(-1)
    }

    static SiteNodeContainerInfo root = new DirectoryInfo.Builder()
            .withName('/')
            .withPath(null)
            .withParent(null)
            .withLocalizedName('/')
            .build()

    static SiteNodeContainerInfo a = new DirectoryInfo.Builder()
            .withName('A')
            .withPath('/')
            .withParent(root)
            .withLocalizedName('a')
            .build()

    static SiteNodeContainerInfo b = new DirectoryInfo.Builder()
            .withName('B')
            .withPath('/')
            .withParent(root)
            .withLocalizedName('b')
            .build()

    static SiteNodeInfo a1 = new PageInfo.Builder()
            .withName('a1')
            .withPath('/a/')
            .withParent(a)
            .withLocalizedName('a_1')
            .withTemplateReference(templateReference)
            .withCompileState(compileState)
            .build()

    static SiteNodeInfo a2 = new PageInfo.Builder()
            .withName('a2')
            .withPath('/a/')
            .withParent(a)
            .withLocalizedName('a_2')
            .withTemplateReference(templateReference)
            .withCompileState(compileState)
            .build()

    static SiteNodeInfo b1 = new PageInfo.Builder()
            .withName('b1')
            .withPath('/b/')
            .withParent(b)
            .withLocalizedName('b_1')
            .withTemplateReference(templateReference)
            .withCompileState(compileState)
            .build()

    static SiteNodeContainerInfo b2 = new DirectoryInfo.Builder()
            .withName('b2')
            .withPath('/b/')
            .withParent(b)
            .withLocalizedName('b_2')
            .build()

    static SiteNodeInfo b21 = new PageInfo.Builder()
            .withName('b21')
            .withPath('/b/b2/')
            .withParent(b2)
            .withLocalizedName('b_2_1')
            .withTemplateReference(templateReference)
            .withCompileState(compileState)
            .build()

    @Unroll
    'filename resolving for #node returns #expectedFilename'() {
        given:
        DefaultFilenameStrategy strategy = new DefaultFilenameStrategy()

        when:
        String filename = strategy.getFilename(node)

        then:
        filename == expectedFilename

        where:
        node | expectedFilename
        root | null
        a    | null
        a1   | 'a_1'
        a2   | 'a_2'
        b    | null
        b1   | 'b_1'
        b2   | null
        b21  | 'b_2_1'
    }

    @Unroll
    'folder resolving for #node returns #expectedFolder'() {
        given:
        DefaultFilenameStrategy strategy = new DefaultFilenameStrategy()

        when:
        String folder = strategy.getFolder(node)

        then:
        folder == expectedFolder

        where:
        node | expectedFolder
        root | '/'
        a    | '/a'
        a1   | '/a'
        a2   | '/a'
        b    | '/b'
        b1   | '/b'
        b2   | '/b/b_2'
        b21  | '/b/b_2'
    }
}
