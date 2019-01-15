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
            .withLocalizedName(null)
            .build()

    static SiteNodeContainerInfo d_a = new DirectoryInfo.Builder()
            .withName('A')
            .withPath('/')
            .withParent(root)
            .withLocalizedName(['en': 'd.a'])
            .build()

    static SiteNodeContainerInfo d_b = new DirectoryInfo.Builder()
            .withName('B')
            .withPath('/')
            .withParent(root)
            .withLocalizedName(['en': 'd.b'])
            .build()

    static SiteNodeInfo p_a1 = new PageInfo.Builder()
            .withName('a1')
            .withPath('/a/')
            .withParent(d_a)
            .withLocalizedName(['en': 'p.a_1'])
            .withTemplateReference(templateReference)
            .build()

    static SiteNodeInfo p_a2 = new PageInfo.Builder()
            .withName('a2')
            .withPath('/a/')
            .withParent(d_a)
            .withLocalizedName(['en': 'p.a_2'])
            .withTemplateReference(templateReference)
            .build()

    static SiteNodeInfo p_b1 = new PageInfo.Builder()
            .withName('b1')
            .withPath('/b/')
            .withParent(d_b)
            .withLocalizedName(['en': 'p.b_1'])
            .withTemplateReference(templateReference)
            .build()

    static SiteNodeContainerInfo d_b2 = new DirectoryInfo.Builder()
            .withName('b2')
            .withPath('/b/')
            .withParent(d_b)
            .withLocalizedName(['en': 'd.b_2'])
            .build()

    static SiteNodeInfo p_b21 = new PageInfo.Builder()
            .withName('b21')
            .withPath('/b/b2/')
            .withParent(d_b2)
            .withLocalizedName(['en': 'p.b_2_1'])
            .withTemplateReference(templateReference)
            .build()

    static {
        root.nodes = [d_a, d_b]

        d_a.nodes = [p_a1, p_a2]

        d_b.nodes = [p_b1, d_b2]

        d_b2.nodes = [p_b21]
    }

    @Unroll
    'filename resolving (without index-replacement) for (#node.path, #node.localizedName) returns #expectedFilename'() {
        given:
        DefaultFilenameStrategy strategy

        when:
        strategy = new DefaultFilenameStrategy(false)

        then:
        strategy.getFilename('en', node) == expectedFilename

        where:
        node   | expectedFilename
        root   | null
        d_a    | 'd.a'
        p_a1   | 'p.a_1'
        p_a2   | 'p.a_2'
        d_b    | 'd.b'
        p_b1   | 'p.b_1'
        d_b2   | 'd.b_2'
        p_b21  | 'p.b_2_1'
    }

    @Unroll
    'filename resolving (with index-replacement) for (#node.path, #node.localizedName) returns #expectedFilename'() {
        given:
        DefaultFilenameStrategy strategy

        when:
        strategy = new DefaultFilenameStrategy(true)

        then:
        strategy.getFilename('en', node) == expectedFilename

        where:
        node   | expectedFilename
        root   | null
        d_a    | 'd.a'
        p_a1   | 'index'
        p_a2   | 'p.a_2'
        d_b    | 'd.b'
        p_b1   | 'index'
        d_b2   | 'd.b_2'
        p_b21  | 'index'
    }

    @Unroll
    'folder resolving for (#node.path, #node.localizedName) returns #expectedFolder'() {
        given:
        DefaultFilenameStrategy strategy = new DefaultFilenameStrategy()

        when:
        String folder = strategy.getFolder('en', node)

        then:
        folder == expectedFolder

        where:
        node   | expectedFolder
//        d_a    | '/d.a'
//        p_a1   | '/d.a'
//        p_a2   | '/d.a'
//        d_b    | '/d.b'
//        p_b1   | '/d.b'
//        d_b2   | '/d.b/d.b_2'
//        p_b21  | '/d.b/d.b_2'

        root   | '/'
        d_a    | '/'
        p_a1   | '/d.a'
        p_a2   | '/d.a'
        d_b    | '/'
        p_b1   | '/d.b'
        d_b2   | '/d.b'
        p_b21  | '/d.b/d.b_2'
    }
}
