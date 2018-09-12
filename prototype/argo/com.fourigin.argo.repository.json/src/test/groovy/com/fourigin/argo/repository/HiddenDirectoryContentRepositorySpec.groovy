package com.fourigin.argo.repository

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule
import com.fourigin.argo.models.datasource.index.DataSourceIndex
import com.fourigin.argo.models.structure.CompileState
import com.fourigin.argo.models.structure.PageState
import com.fourigin.argo.models.structure.nodes.DirectoryInfo
import com.fourigin.argo.models.structure.nodes.PageInfo
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo
import com.fourigin.argo.models.template.TemplateReference
import com.fourigin.argo.repository.model.JsonDirectoryInfo
import com.fourigin.argo.repository.model.JsonFileInfo
import com.fourigin.argo.repository.model.JsonInfoList
import com.fourigin.argo.repository.model.mapping.JsonInfoModule
import org.apache.commons.io.FileUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class HiddenDirectoryContentRepositorySpec extends Specification {

    @Shared
    String rootPath = new File('src/test/resources/content-root').absolutePath

    @Shared
    ObjectMapper objectMapper = createObjectMapper()

    @Unroll
    'getHiddenDirectory(#path) returns #expectedResult'() {
        when:
        HiddenDirectoryContentRepository repository = new HiddenDirectoryContentRepository()
        repository.setContentRoot(rootPath)
        repository.setObjectMapper(objectMapper)

        def hidden = repository.getHiddenDirectory(path)

        then:
        hidden.absolutePath == expectedResult

        where:
        path              | expectedResult
        '/'               | rootPath + '/.cms'
        '/home'           | rootPath + '/.cms'
        '/regions'        | rootPath + '/regions/.cms'
        '/regions/athen'  | rootPath + '/regions/.cms'
        '/objects/rent'   | rootPath + '/objects/rent/.cms'
        '/objects/rent/1' | rootPath + '/objects/rent/.cms'
    }

    def 'getHiddenDirectory() returns null for a non-existing path'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()

        when:
        def hidden = repository.getHiddenDirectory("/a-non-existing-path")

        then:
//        hidden == null
        IllegalArgumentException ex = thrown()
        ex.message == "HIDDEN \'/a-non-existing-path\', resolved to " + repository.contentRoot.absolutePath + "/a-non-existing-path.json" + " is invalid!"
    }

    def 'getSiteStructureAttributesFile() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        String rootPath = repository.contentRoot.absolutePath

        when:
        def siteStructureAttributesFile = repository.getSiteStructureAttributesFile()

        then:
        siteStructureAttributesFile.absolutePath == rootPath + '/.site'
    }

    def 'readSiteStructureAttributesFile() works as expected'() {
        setup:
        Map<String, String> attributes = new HashMap<>()
        attributes['a'] = 'A'
        attributes['b'] = 'B'
        attributes['c'] = 'C'

        HiddenDirectoryContentRepository repository = initRepository()
        writeSiteStructureAttributesFile(repository.contentRoot, attributes)

        when:
        def siteStructureAttributes = repository.readSiteStructureAttributes()

        then:
        siteStructureAttributes == attributes
    }

    def 'writeSiteStructureAttributesFile() works as expected'() {
        setup:
        Map<String, String> attributes = new HashMap<>()
        attributes['a'] = 'A'
        attributes['b'] = 'B'
        attributes['c'] = 'C'

        HiddenDirectoryContentRepository repository = initRepository()

        when:
        repository.writeSiteStructureAttributes(attributes)

        and:
        def siteStructureAttributes = repository.readSiteStructureAttributes()

        then:
        siteStructureAttributes == attributes
    }

    def 'getDirectoryInfoFile() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        File someDir = File.createTempDir()

        when:
        def directoryInfoFile = repository.getDirectoryInfoFile(someDir)

        then:
        directoryInfoFile.absolutePath == someDir.absolutePath + '/.info'
    }

    def 'readDirectoryInfoFile() works as expected'() {
        setup:
        JsonInfoList infoList = new JsonInfoList("somePath")
        infoList.setChildren([
                createJsonFileInfo('a'),
                createJsonFileInfo('b'),
                createJsonFileInfo('c')
        ])

        File someDir = File.createTempDir()

        HiddenDirectoryContentRepository repository = initRepository()
        writeDirectoryInfoFile(someDir, infoList)

        File infoFile = new File(someDir, '.info')

        when:
        def readInfoList = repository.readDirectoryInfoFile(infoFile)

        then:
        readInfoList == infoList
    }

    def 'writeDirectoryInfoFile() works as expected'() {
        setup:
        JsonInfoList infoList = new JsonInfoList("somePath")
        infoList.setChildren([
                createJsonFileInfo('a'),
                createJsonFileInfo('b'),
                createJsonFileInfo('c')
        ])

        File someDir = File.createTempDir()

        HiddenDirectoryContentRepository repository = initRepository()

        File infoFile = new File(someDir, '.info')

        when:
        repository.writeDirectoryInfoFile(infoFile, infoList)

        and:
        def readInfoList = readDirectoryInfoFile(someDir)

        then:
        readInfoList == infoList
    }

    def 'getPageStateFile() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        File hiddenDir = repository.getHiddenDirectory('/')

        when:
        def pageStateFile = repository.getPageStateFile(hiddenDir, 'page-name')

        then:
        pageStateFile.absolutePath == hiddenDir.absolutePath + '/page-name_info.json'
    }

    def 'readPageStateFile() works as expected'() {
        setup:
        PageState pageState = new PageState()
        pageState.staged = true
        pageState.revision = '1'
        pageState.setChecksum('meta', 'content', null)
        CompileState compileState = new CompileState()
        compileState.checksum = 'checksum'
        compileState.compiled = true
        compileState.message = 'message'
        compileState.timestamp = -1
        pageState.compileState = compileState

        File someDir = File.createTempDir()

        HiddenDirectoryContentRepository repository = initRepository()
        writePageStateFile(someDir, 'page-name', pageState)

        File pageStateFile = new File(someDir, 'page-name_info.json')

        when:
        def readPageState = repository.readPageStateFile(pageStateFile)

        then:
        readPageState == pageState
    }

    def 'writePageStateFile() works as expected'() {
        setup:
        PageState pageState = new PageState()
        pageState.staged = true
        pageState.revision = '1'
        pageState.setChecksum('meta', 'content', null)
        CompileState compileState = new CompileState()
        compileState.checksum = 'checksum'
        compileState.compiled = true
        compileState.message = 'message'
        compileState.timestamp = -1
        pageState.compileState = compileState

        File someDir = File.createTempDir()
        File pageStateFile = new File(someDir, 'page-name_info.json')

        HiddenDirectoryContentRepository repository = initRepository()

        when:
        repository.writePageStateFile(pageStateFile, pageState)

        and:
        def readPageState = repository.readPageStateFile(pageStateFile)

        then:
        readPageState == pageState
    }

    def 'initialize() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        Date beforeInit = new Date() // current timestamp

        expect:
        repository.initTimestamp == null

        when:
        repository.initialize()

        then:
        beforeInit.before(repository.initTimestamp)

        when:
        def beforeReInit = repository.initTimestamp

        and:
        repository.initialize()

        then:
        beforeReInit.before(repository.initTimestamp)
    }

    // create the tree & initialize it on-the-fly!
//    def 'ensureInit() works as expected'() {
//        setup:
//        HiddenDirectoryContentRepository repository = initRepository()
//
//        when:
//        repository.ensureInit()
//
//        and:
//        def expected = stubRoot(false)
//
//        then:
//        repository.initTimestamp != null
//        repository.root == expected
//    }

    def 'createInfo() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        File contentRoot = repository.getContentRoot()

        when:
        PageInfo pageInfo1 = createPageInfo('1', null)
        repository.createInfo('/', pageInfo1)

        and:
        JsonInfoList storedDirInfo1 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        then:
        storedDirInfo1.path == '/'
        storedDirInfo1.children.size() == 6
        storedDirInfo1.children.contains(new JsonFileInfo(pageInfo1))

        when:
        PageInfo pageInfo2 = createPageInfo('2', null)
        repository.createInfo('/', pageInfo2)

        and:
        JsonInfoList storedDirInfo2 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        then:
        storedDirInfo2.path == '/'
        storedDirInfo2.children.size() == 7
        storedDirInfo2.children.contains(new JsonFileInfo(pageInfo1))
        storedDirInfo2.children.contains(new JsonFileInfo(pageInfo2))

        when:
        DirectoryInfo parent2 = new DirectoryInfo(
                name: 'dir',
                parent: null,
                path: '/',
                displayName: 'dir'
        )
        repository.createInfo('/', parent2)

        and:
        JsonInfoList storedDirInfo3 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        then:
        storedDirInfo3.path == '/'
        storedDirInfo3.children.size() == 8
        storedDirInfo3.children.contains(new JsonFileInfo(pageInfo1))
        storedDirInfo3.children.contains(new JsonFileInfo(pageInfo2))
        storedDirInfo3.children.contains(new JsonDirectoryInfo(parent2))
        new File(contentRoot, 'dir').exists()
        new File(contentRoot, 'dir').isDirectory()

        when:
        PageInfo pageInfo3 = createPageInfo('3', parent2)
        repository.createInfo('/dir', pageInfo3)

        and:
        File storeDir = repository.getHiddenDirectory('/dir')
        JsonInfoList storedDirInfo4 = readDirectoryInfoFile(storeDir)

        then:
        storedDirInfo4.path == '/dir'
        storedDirInfo4.children.size() == 1
        storedDirInfo4.children.contains(new JsonFileInfo(pageInfo3))
    }

    def 'resolveInfo() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()

        when:
        def homeInfo = repository.resolveInfo(PageInfo.class, '/home')

        then:
        homeInfo == new PageInfo(
                path: '/',
                name: 'home',
                parent: repository.root,
                description: 'Auto-created directory info. Don\'t forget to specify all properties!',
                localizedName: 'home',
                displayName: 'home',
                templateReference: new TemplateReference(
                        templateId: 'greekestate.index',
                        variationId: 'default',
                        revision: ''
                )
        )
    }

    def 'resolveInfo() and updateInfo() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()

        when:
        def homeInfo = repository.resolveInfo(PageInfo.class, '/home')

        and:
        homeInfo.description = 'changed description'

        and:
        repository.updateInfo('/', homeInfo)

        and:
        JsonInfoList storedDirInfo1 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        then:
        storedDirInfo1.path == '/'
        storedDirInfo1.children.size() == 5
        storedDirInfo1.children.contains(new JsonFileInfo(homeInfo))

        when:
        def objectsInfo = repository.resolveInfo(DirectoryInfo.class, '/objects')

        and:
        objectsInfo.description = 'changed description'

        and:
        repository.updateInfo('/', objectsInfo)

        and:
        JsonInfoList storedDirInfo2 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        then:
        storedDirInfo2.path == '/'
        storedDirInfo2.children.size() == 5
        storedDirInfo2.children.contains(new JsonFileInfo(homeInfo))
        storedDirInfo2.children.contains(new JsonDirectoryInfo(objectsInfo))
    }

    def 'deleteInfo() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()

        when:
        repository.deleteInfo('/home')

        and:
        JsonInfoList storedDirInfo1 = readDirectoryInfoFile(getHiddenDirectory(repository.contentRoot))

        and:
        File contentFile = new File(repository.contentRoot, 'home.json')
        File deletedContainer = new File(repository.contentRoot, ".trash")
        File deletedContentFile = new File(deletedContainer, "home.json")

        then:
        storedDirInfo1.path == '/'
        storedDirInfo1.children.size() == 4
        !contentFile.exists()
        deletedContentFile.exists()
    }

    def 'getIndexFile() works as expected'() {
        setup:
        HiddenDirectoryContentRepository repository = initRepository()
        File hiddenDir = repository.getHiddenDirectory('/')

        when:
        def indexFile = repository.getIndexFile(hiddenDir, 'page-name', 'index-name')

        then:
        indexFile.absolutePath == hiddenDir.absolutePath + '/page-name_index-name_index.json'
    }

    def 'readIndexFile() works as expected'() {
        setup:
        DataSourceIndex index = new DataSourceIndex(
                name: 'index-name-1',
                categories: [],
                fields: [],
                searchValues: []
        )

        File someDir = File.createTempDir()

        HiddenDirectoryContentRepository repository = initRepository()
        writeIndexFile(someDir, 'page-name', index)

        File indexFile = new File(someDir, 'page-name_index-name-1_index.json')

        when:
        def readIndex = repository.readIndexFile(indexFile)

        then:
        readIndex == index
    }

    def 'writeIndexFile() works as expected'() {
        setup:
        DataSourceIndex index = new DataSourceIndex(
                name: 'index-name-1',
                categories: [],
                fields: [],
                searchValues: []
        )

        File someDir = File.createTempDir()
        File indexFile = new File(someDir, 'page-name_index-name-1_index.json')

        HiddenDirectoryContentRepository repository = initRepository()

        when:
        repository.writeIndexFile(indexFile, index)

        and:
        def readIndex = repository.readIndexFile(indexFile)

        then:
        readIndex == index
    }

    // Helper methods

    HiddenDirectoryContentRepository initRepository() {
        File cloneRoot = cloneContentRoot()

        HiddenDirectoryContentRepository repository = new HiddenDirectoryContentRepository()
        repository.setContentRoot(cloneRoot.absolutePath)
        repository.setObjectMapper(objectMapper)

        return repository
    }

    File cloneContentRoot() {
        File target = File.createTempDir()
        File source = new File(rootPath)
        FileUtils.copyDirectory(source, target)
        return target
    }

    File getHiddenDirectory(File dir) {
        return new File(dir, '.cms')
    }

    void writeSiteStructureAttributesFile(File contentRoot, Map<String, String> structure) {
        File structureFile = new File(contentRoot, '.site')

        OutputStream os = new BufferedOutputStream(new FileOutputStream(structureFile))

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, structure)
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.absolutePath + ")!", ex)
        }
    }

    JsonInfoList readDirectoryInfoFile(File dir) {
        File infoFile = new File(dir, '.info')

        InputStream is = new BufferedInputStream(new FileInputStream(infoFile))

        try {
            return objectMapper.readValue(is, JsonInfoList.class)
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error reading directory info file (" + infoFile.absolutePath + ")!", ex)
        }
    }

    void writeDirectoryInfoFile(File dir, JsonInfoList infoList) {
        File infoFile = new File(dir, '.info')

        OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, infoList)
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing directory info file (" + infoFile.absolutePath + ")!", ex)
        }
    }

    JsonFileInfo createJsonFileInfo(String code) {
        JsonFileInfo info = new JsonFileInfo()

        info.setName(code + '-name')
        info.setDescription(code + '-description')
        info.setLocalizedName(code + '-localizedName')
        info.setDisplayName(code + '-displayName')

        TemplateReference templateReference = new TemplateReference()
        templateReference.revision = code + 'revision'
        templateReference.templateId = code + '-template'
        templateReference.variationId = 'default'
        info.setTemplateReference(templateReference)

        return info
    }

    void writePageStateFile(File dir, String name, PageState pageState) {
        File pageStateFile = new File(dir, name + '_info.json')

        OutputStream os = new BufferedOutputStream(new FileOutputStream(pageStateFile))

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, pageState)
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing page-state file (" + pageStateFile.absolutePath + ")!", ex)
        }
    }

    PageInfo createPageInfo(String id, SiteNodeContainerInfo parent) {
        return createPageInfo(id, parent, true)
    }

    PageInfo createPageInfo(String id, SiteNodeContainerInfo parent, boolean fullInitialized) {
        String path
        if(parent == null) {
            path = '/'
        }
        else if(parent.name == null) {
            path = '/'
        }
        else if(parent.path == '/') {
            path = '/' + parent.name
        }
        else {
            path = parent.path + '/' + parent.name
        }

        PageInfo.Builder builder = new PageInfo.Builder()
                .withName(id)
                .withPath(path)
                .withParent(parent)

        if (fullInitialized) {
            builder
                    .withDisplayName('display-name-' + id)
                    .withLocalizedName('localized-name-' + id)
                    .withDescription('description-' + id)
                    .withTemplateReference(
                    new TemplateReference(
                            templateId: 'some-template',
                            variationId: 'default',
                            revision: '1'
                    )
            )
        } else {
            builder
//                    .withDisplayName(id)
//                    .withLocalizedName(id)
//                    .withDescription('Auto-created directory info. Don\'t forget to specify all properties!')
                    .withTemplateReference(
                    new TemplateReference(
                            templateId: null,
                            variationId: null,
                            revision: null
                    )
            )
        }

        return builder.build()
    }

    DirectoryInfo createDirectoryInfo(String id, SiteNodeContainerInfo parent) {
        return createDirectoryInfo(id, parent, true)
    }

    DirectoryInfo createDirectoryInfo(String id, SiteNodeContainerInfo parent, boolean fullInitialized) {
        String path
        if(parent == null) {
            path = '/'
        }
        else if(parent.name == null) {
            path = '/'
        }
        else if(parent.path == '/') {
            path = '/' + parent.name
        }
        else {
            path = parent.path + '/' + parent.name
        }

        DirectoryInfo.Builder builder = new DirectoryInfo.Builder()
                .withName(id)
                .withPath(path)
                .withParent(parent)

        if (fullInitialized) {
            builder
                    .withDisplayName('display-name-' + id)
                    .withLocalizedName('localized-name-' + id)
                    .withDescription('description-' + id)
        }
        else {
            builder
                    .withDisplayName(id)
                    .withLocalizedName(id)
                    .withDescription('Auto-created directory info. Don\'t forget to specify all properties!')
        }

        return builder.build()
    }

    void writeIndexFile(File dir, String pageName, DataSourceIndex index) {
        File indexFile = new File(dir, pageName + '_' + index.name + '_index.json')

        OutputStream os = new BufferedOutputStream(new FileOutputStream(indexFile))

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, index)
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing index file (" + indexFile.absolutePath + ")!", ex)
        }
    }

    ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper(
                serializationInclusion: JsonInclude.Include.NON_NULL
        )
        objectMapper.registerModule(new ContentPageModule())
        objectMapper.registerModule(new JsonInfoModule())
        return objectMapper
    }

    SiteNodeContainerInfo stubRoot(boolean fullInitialized) {
        DirectoryInfo root = new DirectoryInfo(
                path: ''
        )

        DirectoryInfo dir_objects = createDirectoryInfo('objects', root, fullInitialized)

        PageInfo page_objects_overview = createPageInfo('overview', dir_objects, fullInitialized)

        DirectoryInfo dir_rent = createDirectoryInfo('rent', dir_objects, fullInitialized)

        PageInfo page_rent_1 = createPageInfo('1', dir_rent, fullInitialized)

        PageInfo page_rent_2 = createPageInfo('2', dir_rent, fullInitialized)

        PageInfo page_rent_3 = createPageInfo('3', dir_rent, fullInitialized)

        dir_rent.nodes = [
                page_rent_1,
                page_rent_2,
                page_rent_3
        ]

        DirectoryInfo dir_sale = createDirectoryInfo('sale', dir_objects, fullInitialized)

        PageInfo page_sale_1 = createPageInfo('1', dir_sale, fullInitialized)

        PageInfo page_sale_2 = createPageInfo('2', dir_sale, fullInitialized)

        PageInfo page_sale_3 = createPageInfo('3', dir_sale, fullInitialized)

        dir_sale.nodes = [
                page_sale_1,
                page_sale_2,
                page_sale_3
        ]

        dir_objects.nodes = [
                page_objects_overview,
                dir_rent,
                dir_sale
        ]

        DirectoryInfo dir_regions = createDirectoryInfo('regions', root, fullInitialized)

        PageInfo page_region_athen = createPageInfo('athen', dir_regions, fullInitialized)

        PageInfo page_region_loutraki = createPageInfo('loutraki', dir_regions, fullInitialized)

        PageInfo page_region_salamina = createPageInfo('salamina', dir_regions, fullInitialized)

        dir_regions.nodes = [
                page_region_athen,
                page_region_loutraki,
                page_region_salamina
        ]

        PageInfo page_about = createPageInfo('about', root, fullInitialized)

        PageInfo page_home = createPageInfo('home', root, fullInitialized)

        PageInfo page_search = createPageInfo('search', root, fullInitialized)

        root.nodes = [
                page_home,
                page_search,
                dir_regions,
                dir_objects,
                page_about
        ]

        return root
    }
}
