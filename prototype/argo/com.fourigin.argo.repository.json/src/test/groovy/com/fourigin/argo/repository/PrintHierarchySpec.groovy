//package com.fourigin.argo.repository
//
//import com.fourigin.argo.config.ContentPageRepositoryConfiguration
//import com.fourigin.argo.models.structure.CompileState
//import com.fourigin.argo.models.structure.nodes.DirectoryInfo
//import com.fourigin.argo.models.structure.nodes.PageInfo
//import com.fourigin.argo.models.structure.nodes.SiteNodeInfo
//import com.fourigin.argo.models.template.TemplateReference
//import org.springframework.test.context.ContextConfiguration
//import spock.lang.Specification
//
//@ContextConfiguration(classes=[ContentPageRepositoryConfiguration])
//class PrintHierarchySpec extends Specification {
//
//    def "printHierarchy"() {
//
///*
///
//+--home
//+--objects
//|  +--sale
//|  |  +--object_004
//|  |  +--object_005
//|     +--object_003
//|
//   +--rent
//|  |  +--object_004
//|  |  +--object_002
//|     +--object_001
//|
//
//+--regions
//|  +--salamina
//|  +--athen
//   +--loutraki
//
//+--search
//+--about
//+--impressum
//*/
//
//
//        DirectoryInfo root = createDirectory("", "/", Arrays.asList(
//                createPage("home", "/"),
//                createDirectory("objects", "/", Arrays.asList(
//                        createDirectory("sale", "/objects", Arrays.asList(
//                                createPage("object_004", "/objects/sale"),
//                                createPage("object_005", "/objects/sale"),
//                                createPage("object_003", "/objects/sale")
//                        )),
//                        createDirectory("rent", "/objects", Arrays.asList(
//                                createPage("object_004", "/objects/rent"),
//                                createPage("object_002", "/objects/rent"),
//                                createPage("object_001", "/objects/rent")
//                        ))
//                )),
//                createDirectory("regions", "/", Arrays.asList(
//                        createPage("salamina", "/regions"),
//                        createPage("athen", "/regions"),
//                        createPage("loutraki", "/regions")
//                )),
//                createPage("search", "/"),
//                createPage("about", "/"),
//                createPage("impressum", "/")
//        ))
//
//        String tree = HiddenDirectoryContentRepository.buildInfoTree(root, 0, false)
//        System.out.println(tree)
//
//        expect:
//        tree != null
//    }
//
//    private static DirectoryInfo createDirectory(String name, String path, List<SiteNodeInfo> nodes){
//        DirectoryInfo info = new DirectoryInfo()
//
//        info.setName(name)
//        info.setLocalizedName(name)
//        info.setPath(path)
//        info.setDescription("Auto-created directory info. Don't forget to specify all properties!")
//        info.setDisplayName(name)
//
//        info.setNodes(nodes)
//
//        return info
//    }
//
//    private static PageInfo createPage(String name, String path) {
//        PageInfo info = new PageInfo()
//
//        info.setName(name)
//        info.setLocalizedName(name)
//        info.setPath(path)
//        info.setDescription("Auto-created directory info. Don't forget to specify all properties!")
//        info.setDisplayName(name)
//
//        TemplateReference templateReference = new TemplateReference()
//        templateReference.setTemplateId("unspecified")
//        templateReference.setVariationId("unspecified")
//        templateReference.setRevision("unspecified")
//
//        CompileState compileState = new CompileState()
//        compileState.setChecksum("")
//        compileState.setCompiled(false)
//        compileState.setMessage("")
//        compileState.setTimestamp(new Date().getTime())
//
//        info.setTemplateReference(templateReference)
//
//        return info
//    }
//}
