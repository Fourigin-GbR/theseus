package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.InvalidSiteStructureNodeException;
import com.fourigin.argo.models.InvalidSiteStructurePathException;
import com.fourigin.argo.models.UnsupportedNodeTypeException;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.path.SiteStructurePath;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.model.JsonDirectoryInfo;
import com.fourigin.argo.repository.model.JsonFileInfo;
import com.fourigin.argo.repository.model.JsonInfo;
import com.fourigin.argo.repository.model.JsonInfoList;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.TraversingStrategy;
import com.fourigin.utilities.core.FileBasedRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class HiddenDirectoryContentRepository extends FileBasedRepository implements ContentRepository {

    private static final String HIDDEN_DIRECTORY_NAME = ".cms";

    private static final String SITE_STRUCTURE_FILENAME = ".site";

    private static final String DIRECTORY_INFO_FILENAME = ".info";

    private static final String DELETED_DIRECTORY_NAME = ".trash";

    private static final String PAGE_INFO_FILE_POSTFIX = "_info";

    private static final String PAGE_INDEX_FILE_POSTFIX = "_index";

    private final Logger logger = LoggerFactory.getLogger(HiddenDirectoryContentRepository.class);

    private String id;

    private ObjectMapper objectMapper;

    private String contentRoot;

    private SiteNodeContainerInfo root;

    private Date initTimestamp;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> getDefaultTraversingStrategy() {
        return defaultTraversingStrategy;
    }

//******* SITE methods *******//

    @Override
    public Map<String, String> resolveSiteAttributes() {
        ensureInit();

        ReadWriteLock lock = getLock("/");
        lock.readLock().lock();

        try {
            return readSiteStructureAttributes();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void updateSiteStructureAttributes(Map<String, String> attributes) {
        ensureInit();

        ReadWriteLock lock = getLock("/");
        lock.writeLock().lock();

        try {
            writeSiteStructureAttributes(attributes);
        } finally {
            lock.writeLock().unlock();
        }
    }

    //******* INFO methods *******//

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path) {
        Objects.requireNonNull(path, "Path must not be null!");

        if (logger.isTraceEnabled()) logger.trace("resolveInfo({}, {})", type, path);

        ensureInit();

        return resolveInfo(type, root, path);
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
        Objects.requireNonNull(parent, "Parent must not be null!");
        Objects.requireNonNull(path, "Path must not be null!");

        if (logger.isTraceEnabled()) logger.trace("resolveInfo({}, {}, {})", type, parent.toTreeString(0), path);

        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, parent);
        SiteNodeInfo result = pathPointer.getNodeInfo();

        if (result == null) {
            throw new InvalidSiteStructurePathException(path);
        }

        if (!type.isAssignableFrom(result.getClass())) {
            throw new InvalidSiteStructureNodeException(path, type, result.getClass());
        }

        return type.cast(result);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path) {
        return resolveInfos(path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy) {
        return resolveInfos(root, path, traversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path) {
        return resolveInfos(parent, path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy) {
        Objects.requireNonNull(path, "Path must not be null!");
        Objects.requireNonNull(traversingStrategy, "Traversing strategy must not be null!");

        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, parent);
        SiteNodeInfo info = pathPointer.getNodeInfo();

        if (pathPointer.isDirectory()) {
            SiteNodeContainerInfo dirInfo = (SiteNodeContainerInfo) pathPointer.getNodeInfo();
            return traversingStrategy.collect(dirInfo);
        }

        if (pathPointer.isPage()) {
            PageInfo pageInfo = (PageInfo) pathPointer.getNodeInfo();
            return Collections.singletonList(pageInfo);
        }

        throw new UnsupportedNodeTypeException(info.getClass());
    }

    @Override
    public Collection<SiteNodeInfo> resolveNodeInfos(String path, TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> traversingStrategy) {
        Objects.requireNonNull(path, "Path must not be null!");
        Objects.requireNonNull(traversingStrategy, "Traversing strategy must not be null!");

        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, root);
        SiteNodeInfo info = pathPointer.getNodeInfo();

        if (pathPointer.isDirectory()) {
            SiteNodeContainerInfo dirInfo = (SiteNodeContainerInfo) pathPointer.getNodeInfo();
            //noinspection unchecked
            return (Collection<SiteNodeInfo>) traversingStrategy.collect(dirInfo);
        }

        if (pathPointer.isPage()) {
            PageInfo pageInfo = (PageInfo) pathPointer.getNodeInfo();
            return Collections.singletonList(pageInfo);
        }

        throw new UnsupportedNodeTypeException(info.getClass());
    }

    @Override
    public void createInfo(String path, SiteNodeInfo node) {
        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, root);
        if (!pathPointer.isDirectory()) {
            throw new IllegalArgumentException("'" + path + "' is not a directory! Unable to create a sub node!");
        }

        DirectoryInfo parent = (DirectoryInfo) pathPointer.getNodeInfo();

        createInfo(parent, node);
    }

    @Override
    public void createInfo(SiteNodeContainerInfo parent, SiteNodeInfo node) {
        ensureInit();

        String parentPath = resolveParentPath(parent);
//        String parentName = parent.getName();
//        if(parentName != null) {
//            parentPath += parentName;
//        }

        ReadWriteLock lock = getLock(parentPath);
        lock.writeLock().lock();

        try {
            // reload the content directory
            File hiddenDir = getHiddenDirectory(parentPath);
            File contentDir = hiddenDir.getParentFile();
            processContentDirectory(contentDir, parentPath, parent);

            // add a new node
            List<SiteNodeInfo> children = parent.getNodes();
            if (children == null) {
                children = new ArrayList<>();
            }

            String name = node.getName();
            for (SiteNodeInfo child : children) {
                if (name.equals(child.getName())) {
                    throw new DuplicateSiteNodeFoundException(parent.getPath(), name);
                }
            }

            children.add(node);
            parent.setNodes(children);

            // write the dir-info file
            File dirInfoFile = getDirectoryInfoFile(hiddenDir);

            JsonInfoList infoList;
            try {
                infoList = readDirectoryInfoFile(dirInfoFile);
            }
            catch(Exception ex) {
                infoList = new JsonInfoList(parentPath);
            }

            List<JsonInfo<? extends SiteNodeInfo>> oldChildren = infoList.getChildren();
            if (node instanceof PageInfo) {
                oldChildren.add(new JsonFileInfo((PageInfo) node));
            } else if (node instanceof DirectoryInfo) {
                oldChildren.add(new JsonDirectoryInfo((DirectoryInfo) node));
                File newDir = new File(contentDir, node.getName());
                try {
                    if (!newDir.mkdirs()) {
                        throw new IllegalStateException("without reason ...");
                    }
                }
                catch(Exception ex){
                    throw new IllegalStateException("Unable to create directory '" + newDir.getAbsolutePath() + "'!", ex);
                }
            }
            infoList.setChildren(oldChildren);

            writeDirectoryInfoFile(dirInfoFile, infoList);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateInfo(String path, SiteNodeInfo node) {
        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, root);
        if (!pathPointer.isDirectory()) {
            throw new IllegalArgumentException("'" + path + "' is not a directory! Unable to create a sub node!");
        }

        DirectoryInfo parent = (DirectoryInfo) pathPointer.getNodeInfo();

        updateInfo(parent, node);
    }

    @Override
    public void updateInfo(SiteNodeContainerInfo parent, SiteNodeInfo node) {
        ensureInit();

        if (logger.isTraceEnabled()) logger.trace("updateInfo({}, {})", parent.toTreeString(0), node);

        String parentPath = resolveParentPath(parent);

        ReadWriteLock lock = getLock(parentPath);
        lock.writeLock().lock();

        try {
            // reload the content directory
            File hiddenDir = getHiddenDirectory(parentPath);
            File contentDir = hiddenDir.getParentFile();
            processContentDirectory(contentDir, parentPath, parent);

            if (logger.isDebugEnabled()) logger.debug("Parent after reloading the content directory:\n{}", parent.toTreeString(0));

            // add a new node
            List<SiteNodeInfo> children = parent.getNodes();
            if (children == null) {
                children = new ArrayList<>();
            }

            String name = node.getName();
            SiteNodeInfo match = null;
            for (SiteNodeInfo child : children) {
                if (name.equals(child.getName())) {
                    match = child;
                    break;
                }
            }

            if (match == null) {
                throw new IllegalStateException("No info node found for name '" + name + "'!");
            }

            int pos = children.indexOf(match);
            children.remove(match);
            children.add(pos, node);
            parent.setNodes(children);

            // write the dir-info file
            File dirInfoFile = getDirectoryInfoFile(hiddenDir);

            JsonInfoList infoList = readDirectoryInfoFile(dirInfoFile);
            List<JsonInfo<? extends SiteNodeInfo>> oldChildren = infoList.getChildren();

            List<JsonInfo<? extends SiteNodeInfo>> newChildren = new ArrayList<>();
            for (JsonInfo<? extends SiteNodeInfo> child : oldChildren) {
                if (child.getName().equals(node.getName())) {
                    JsonInfo<? extends SiteNodeInfo> changedInfo = null;

                    if (node instanceof PageInfo) {
                        changedInfo = new JsonFileInfo((PageInfo) node);
                    } else if (node instanceof DirectoryInfo) {
                        changedInfo = new JsonDirectoryInfo((DirectoryInfo) node);
                    }
                    newChildren.add(changedInfo);
                } else {
                    newChildren.add(child);
                }
            }
            infoList.setChildren(newChildren);

            writeDirectoryInfoFile(dirInfoFile, infoList);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteInfo(String path) {
        ensureInit();

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, root);
        SiteNodeInfo node = pathPointer.getNodeInfo();

        SiteNodeContainerInfo parent = node.getParent();
        String nodeName = node.getName();

        deleteInfo(parent, nodeName);
    }

    @Override
    public void deleteInfo(SiteNodeContainerInfo parent, String nodeName) {
        ensureInit();

        String parentPath = resolveParentPath(parent);

        ReadWriteLock lock = getLock(parentPath);
        lock.writeLock().lock();

        try {
            // reload the content directory
            File hiddenDir = getHiddenDirectory(parentPath);
            File contentDir = hiddenDir.getParentFile();
            processContentDirectory(contentDir, parentPath, parent);

            File deletedContainer = new File(contentDir, DELETED_DIRECTORY_NAME);
            if(!deletedContainer.exists() && !deletedContainer.mkdirs()) {
                throw new IllegalStateException("Unable to create a directory '" + deletedContainer.getAbsolutePath() + "'!");
            }

            // add a new node
            List<SiteNodeInfo> children = parent.getNodes();
            if (children == null) {
                children = new ArrayList<>();
            }

            SiteNodeInfo match = null;
            for (SiteNodeInfo child : children) {
                if (nodeName.equals(child.getName())) {
                    match = child;
                    break;
                }
            }

            if(match == null) {
                throw new IllegalStateException("No info node found for name '" + nodeName + "'!");
            }

            children.remove(match);
            parent.setNodes(children);

            if(match instanceof PageInfo) {
                String fileName = nodeName + ".json";
                File contentFile = new File(contentDir, fileName);
                if(!contentFile.renameTo(new File(deletedContainer, fileName))) {
                    throw new IllegalStateException("Unable to delete file '" + fileName + "'! Error while moving it to the deleted container '" + deletedContainer.getAbsolutePath() + "'!");
                }
            }
            else if(match instanceof DirectoryInfo) {
                File dir = new File(contentDir, nodeName);
                if(!dir.renameTo(new File(deletedContainer, nodeName))) {
                    throw new IllegalStateException("Unable to delete directory '" + nodeName + "'! Error while moving it to the deleted container '" + deletedContainer.getAbsolutePath() + "'!");
                }
            }
            else {
                throw new UnsupportedNodeTypeException(match.getClass());
            }

            // write the dir-info file
            File dirInfoFile = getDirectoryInfoFile(hiddenDir);

            JsonInfoList infoList = readDirectoryInfoFile(dirInfoFile);
            List<JsonInfo<? extends SiteNodeInfo>> oldChildren = infoList.getChildren();

            List<JsonInfo<? extends SiteNodeInfo>> newChildren = new ArrayList<>();
            for (JsonInfo<? extends SiteNodeInfo> child : oldChildren) {
                if (child.getName().equals(nodeName)) {
                    continue;
                }

                newChildren.add(child);
            }
            infoList.setChildren(newChildren);

            writeDirectoryInfoFile(dirInfoFile, infoList);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    //******* PAGE STATE methods *******//

    @Override
    public PageState resolvePageState(PageInfo pageInfo) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageStateFile(hiddenDir, name);

        PageState result = readPageStateFile(pageInfoFile);
        if(result == null) {
            if (logger.isDebugEnabled()) logger.debug("No PageState file found for {}, creating an empty initial state.", pageInfo);
            result = new PageState.Builder()
                .withMetaDataChecksum("")
                .withContentChecksum("")
                .withDataSourceChecksum(new TreeMap<>())
                .withStaged(false)
                .build();
        }

        return result;
    }

    @Override
    public void createPageState(PageInfo pageInfo, PageState pageState) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageStateFile(hiddenDir, name);

        writePageStateFile(pageInfoFile, pageState);
    }

    @Override
    public void updatePageState(PageInfo pageInfo, PageState pageState) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageStateFile(hiddenDir, name);

        writePageStateFile(pageInfoFile, pageState);
    }

    @Override
    public void deletePageState(PageInfo pageInfo) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageStateFile(hiddenDir, name);

        if (!pageInfoFile.delete()) {
            throw new IllegalStateException("Unable to delete page info file '" + pageInfoFile.getAbsolutePath() + "'!");
        }
    }

    //******* PAGE methods *******//

    @Override
    public ContentPage retrieve(PageInfo info) {
        Objects.requireNonNull(info, "PageInfo must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Retrieving ContentPage for info '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.readLock().lock();

        try {
            File contentFile = getContentPageFile(info);
            String fullPath = contentFile.getAbsolutePath();
            if (!contentFile.exists()) {
                if (logger.isInfoEnabled())
                    logger.info("Content file {} does not exist!", fullPath);

                return null;
            }

            return readContentPage(info, contentFile);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void create(PageInfo info, ContentPage contentPage) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Creating ContentPage for '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentPageFile(info);
            if (contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does already exist!", contentFile.getAbsolutePath());

                return;
            }

            writeContentPage(info, contentPage, contentFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(PageInfo info, ContentPage contentPage) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Updating ContentPage for info '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentPageFile(info);
            if (!contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());

                return;
            }

            if (logger.isDebugEnabled()) logger.debug("Writing result to content file '{}': {}", contentFile.getAbsolutePath(), contentPage);
            writeContentPage(info, contentPage, contentFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(PageInfo info) {
        Objects.requireNonNull(info, "PageInfo must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Deleting ContentPage for parent '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentPageFile(info);
            if (!contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());

                return;
            }

            boolean deleted = contentFile.delete();
            if (!deleted) {
                throw new IllegalStateException("Unable to delete content-file '" + contentFile.getAbsolutePath() + "'!");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    //******* INDEX methods *******//

    @Override
    public Set<String> listIndexes(PageInfo info) {
        Objects.requireNonNull(info, "PageInfo must not be null!");

        ensureInit();

        String pageName = info.getName();
        String path = info.getPath();

        File hiddenDir = getHiddenDirectory(path);
        Map<String, File> indexFiles = getIndexFiles(hiddenDir, pageName);

        return indexFiles.keySet();
    }

    @Override
    public DataSourceIndex resolveIndex(PageInfo info, String indexName) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(indexName, "Index name must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Retrieving index '{}' for info '{}'", indexName, info);

        String pageName = info.getName();
        String path = info.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File indexFile = getIndexFile(hiddenDir, pageName, indexName);

        return readIndexFile(indexFile);
    }

    @Override
    public void createIndex(PageInfo info, DataSourceIndex index) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(index, "Index must not be null!");

        ensureInit();

        String pageName = info.getName();
        String path = info.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File indexFile = getIndexFile(hiddenDir, pageName, index.getName());

        writeIndexFile(indexFile, index);
    }

    @Override
    public void deleteIndex(PageInfo info, String indexName) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(indexName, "Index name must not be null!");

        ensureInit();

        String pageName = info.getName();
        String path = info.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File indexFile = getIndexFile(hiddenDir, pageName, indexName);

        if (!indexFile.delete()) {
            throw new IllegalStateException("Unable to delete index file '" + indexFile.getAbsolutePath() + "'!");
        }
    }

    //******* FLUSH methods *******//

    @Override
    public void flush() {
        initialize();
    }

    public Date getInitTimestamp(){
        return initTimestamp;
    }

    //******* private methods *******//

    private String resolveParentPath(SiteNodeContainerInfo parent) {
        String path = parent.getPath();
        String name = parent.getName();
        if(name == null) {
            name = "";
        }

        if("".equals(path)) {
            return "/" + name;
        }

        if(path.endsWith("/")){
            return path + name;
        }
        
        return path + '/' + name;
    }

    /* private -> testing */
    File getSiteStructureAttributesFile() {
        File rootDirectory = new File(contentRoot);

        if(rootDirectory.exists()) {
            if (!rootDirectory.isDirectory()) {
                throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
            }
        } else if(!rootDirectory.mkdirs()) {
            throw new IllegalArgumentException("Unable to create a root directory '" + rootDirectory.getAbsolutePath() + "'!");
        }

        File siteStructureFile = new File(rootDirectory, SITE_STRUCTURE_FILENAME);

        if (logger.isTraceEnabled())
            logger.trace("Resolved site-structure file '{}'", siteStructureFile.getAbsolutePath());

        return siteStructureFile;
    }

    /* private -> testing */
    Map<String, String> readSiteStructureAttributes() {
        File structureFile = getSiteStructureAttributesFile();

        SiteStructureAttributes result;

        try (InputStream is = new BufferedInputStream(new FileInputStream(structureFile))) {
            result = objectMapper.readValue(is, SiteStructureAttributes.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Error reading site-structure attributes ({})!", structureFile.getAbsolutePath(), ex);
            result = null;
        }

        if (result == null) {
            result = new SiteStructureAttributes();

            if (logger.isDebugEnabled()) logger.debug("Creating empty site attributes.");
            writeSiteStructureAttributes(result);
        }

        return result;
    }

    /* private -> testing */
    void writeSiteStructureAttributes(Map<String, String> structure) {
        File structureFile = getSiteStructureAttributesFile();

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(structureFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, structure);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
        }
    }

    /* private -> testing */
    File getHiddenDirectory(String path) {
        File rootDirectory = getContentRoot();

        String normalizedPath = path.trim();

        File target;
        if ("/".equals(normalizedPath)) {
            target = rootDirectory;
        } else {
            // try to interpret as a directory
            target = new File(rootDirectory, normalizedPath);
        }

        if (!target.exists()) {
            // not a directory, try to interpret as a file (page)
            target = new File(rootDirectory, normalizedPath + ".json");
        }

        if (!target.exists()) {
            throw new IllegalArgumentException("HIDDEN '" + path + "', resolved to " + target.getAbsolutePath() + " is invalid!");
        }

        File hiddenDir;
        if (target.isDirectory()) {
            hiddenDir = new File(target, HIDDEN_DIRECTORY_NAME);
        } else {
            hiddenDir = new File(target.getParent(), HIDDEN_DIRECTORY_NAME);
        }

        String hiddenPath = hiddenDir.getAbsolutePath();

        if (!hiddenDir.exists()) {
            if (logger.isDebugEnabled()) logger.debug("Creating a missing HIDDEN directory '{}'.", hiddenPath);

            //noinspection ResultOfMethodCallIgnored
            hiddenDir.mkdirs();
        }
        if (!hiddenDir.isDirectory()) {
            throw new IllegalArgumentException("HIDDEN '" + hiddenPath + "' is not a directory!");
        }

        if (logger.isDebugEnabled())
            logger.debug("Resolved HIDDEN directory to '{}'", hiddenPath);

        return hiddenDir;
    }

    /* private -> testing */
    File getDirectoryInfoFile(File hiddenDir) {
        return new File(hiddenDir, DIRECTORY_INFO_FILENAME);
    }

    /* private -> testing */
    JsonInfoList readDirectoryInfoFile(File infoFile) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(infoFile))) {
            return objectMapper.readValue(is, JsonInfoList.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error reading info file ({})!", infoFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read info file (" + infoFile.getAbsolutePath() + ")", ex);
        }
    }

    /* private -> testing */
    void writeDirectoryInfoFile(File infoFile, JsonInfoList infoList) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, infoList);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing info file (" + infoFile.getAbsolutePath() + ")!", ex);
        }
    }

    /* private -> testing */
    File getPageStateFile(File hiddenDir, String pageName) {
        String fileName = pageName + PAGE_INFO_FILE_POSTFIX + ".json";

        return new File(hiddenDir, fileName);
    }

    /* private -> testing */
    PageState readPageStateFile(File pageStateFile) {
        if (!pageStateFile.exists()) {
            if (logger.isDebugEnabled()) logger.debug("No pageState file '{}' exists.", pageStateFile.getAbsolutePath());
            return null;
        }

        try (InputStream is = new BufferedInputStream(new FileInputStream(pageStateFile))) {
            return objectMapper.readValue(is, PageState.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Error reading page state file ({})!", pageStateFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read page state file (" + pageStateFile.getAbsolutePath() + ")", ex);
        }
    }

    /* private -> testing */
    void writePageStateFile(File pageStateFile, PageState pageState) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pageStateFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, pageState);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing page state file (" + pageStateFile.getAbsolutePath() + ")!", ex);
        }
    }

    /* private -> testing */
    File getIndexFile(File hiddenDir, String pageName, String indexName){
        String fileName = pageName + '_' + indexName + PAGE_INDEX_FILE_POSTFIX + ".json";

        return new File(hiddenDir, fileName);
    }

    /* private -> testing */
    Map<String, File> getIndexFiles(File hiddenDir, String pageName){
        final String prefix = pageName + "_";
        final String postfix = PAGE_INDEX_FILE_POSTFIX + ".json";

        File[] indexFiles = hiddenDir.listFiles((dir, name)
            -> name.startsWith(prefix) && name.endsWith(postfix));

        Map<String, File> result = new HashMap<>();
        if(indexFiles != null && indexFiles.length > 0){
            int startPos = (prefix).length();

            for (File indexFile : indexFiles) {
                String fileName = indexFile.getName();
                int endPos = fileName.indexOf(postfix);
                String indexName = fileName.substring(startPos, endPos);
                result.put(indexName, indexFile);
            }
        }

        return result;
    }

    /* private -> testing */
    DataSourceIndex readIndexFile(File indexFile) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(indexFile))) {
            return objectMapper.readValue(is, DataSourceIndex.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error reading index file ({})!", indexFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read index file (" + indexFile.getAbsolutePath() + ")", ex);
        }
    }

    /* private -> testing */
    void writeIndexFile(File indexFile, DataSourceIndex index) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(indexFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, index);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing index file (" + indexFile.getAbsolutePath() + ")!", ex);
        }
    }

    /* private -> testing */
    File getContentRoot() {
        File rootDirectory = new File(contentRoot);

        if (!rootDirectory.exists()) {
            if (logger.isDebugEnabled())
                logger.debug("Creating a missing root directory '{}'.", rootDirectory.getAbsolutePath());

            //noinspection ResultOfMethodCallIgnored
            rootDirectory.mkdirs();
        }
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
        }

        if (logger.isDebugEnabled()) logger.debug("Content-root: '{}'", rootDirectory.getAbsolutePath());
        return rootDirectory;
    }

    /* private -> testing */
    File getContentPageFile(PageInfo info) {
        File contentRootFile = new File(contentRoot);

        //noinspection ResultOfMethodCallIgnored
        contentRootFile.mkdirs();
        if (!contentRootFile.isDirectory()) {
            throw new IllegalArgumentException("contentRoot '" + contentRootFile.getAbsolutePath() + "' is not a directory!");
        }

        String filename = info.getName() + ".json";

        // remove first '/'
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        // replace all '/' with '_'
        filename = filename.replace('/', '_');

        File directory = new File(contentRootFile, info.getPath());

        File contentFile = new File(directory, filename);

        if (logger.isDebugEnabled()) logger.debug("Resolved content file '{}'", contentFile.getAbsolutePath());

        return contentFile;
    }

    /* private -> testing */
    ContentPage readContentPage(PageInfo info, File contentFile) {
        InputStream is = null;
        ContentPage result;
        try {
            is = new BufferedInputStream(new FileInputStream(contentFile));
            result = objectMapper.readValue(is, ContentPage.class);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error loading content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(is);
        }

        if (result == null) {
            if (logger.isWarnEnabled())
                logger.warn("Error reading content file '{}'", contentFile.getAbsolutePath());

            return null;
        }

        result.setId(info.getName()); // why?

        return result;
    }

    /* private -> testing */
    void writeContentPage(PageInfo info, ContentPage contentPage, File contentFile) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(contentFile));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, contentPage);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /* private -> testing */
    void initialize() {
        this.root = null;

        ensureInit();
    }

    /* private -> testing */
    void ensureInit() {
        if (root != null) {
            return;
        }

        File rootDir = getContentRoot();

        root = new DirectoryInfo();
        root.setPath("");
        root.setParent(null);

        processContentDirectory(rootDir, "/", root);

        if (logger.isDebugEnabled()) logger.debug("Initialized info:\n{}", root.toTreeString(0));

        initTimestamp = new Date();
        id = ChecksumGenerator.getChecksum(root);
    }

    private void processContentDirectory(File dir, String parentPath, SiteNodeContainerInfo containerInfo) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            // empty directory, TODO
            return;
        }

        File hiddenDir = getHiddenDirectory(parentPath);
        File dirInfoFile = getDirectoryInfoFile(hiddenDir);

        boolean changed = false;
        JsonInfoList infoList;
        if (dirInfoFile.exists()) {
            if (logger.isDebugEnabled()) logger.debug("Reading info file '{}'.", dirInfoFile.getAbsolutePath());
            infoList = readDirectoryInfoFile(dirInfoFile);
        } else {
            if (logger.isDebugEnabled()) logger.debug("Creating a new (empty) info file for path '{}'.", parentPath);
            infoList = new JsonInfoList(parentPath);
            infoList.setChildren(new ArrayList<>());
            changed = true;
        }

        if (logger.isDebugEnabled()) logger.debug("Content of the info file: {}", infoList);

        Map<String, SiteNodeInfo> infos = new HashMap<>();
        Map<String, JsonInfo<? extends SiteNodeInfo>> infoListLookup = infoList.getLookup();

        for (File file : files) {
            String fileName = file.getName();

            if (fileName.startsWith(".")) {
                // skip hidden files
                continue;
            }

            String path = parentPath;
            if(!path.endsWith("/")) {
                path += '/';
            }
            path += fileName;

            if (file.isDirectory()) {
                DirectoryInfo dirInfo = new DirectoryInfo();

                dirInfo.setPath(parentPath);
                dirInfo.setName(fileName);
                // TODO: fill other fields

                JsonInfo<? extends SiteNodeInfo> info = infoListLookup.get(fileName);
                if(info != null) {
                    dirInfo.setDescription(info.getDescription());
                    dirInfo.setDisplayName(info.getDisplayName());
                    dirInfo.setLocalizedName(info.getLocalizedName());
                }

                dirInfo.setParent(containerInfo);

                processContentDirectory(file, path, dirInfo);

                infos.put(fileName, dirInfo);
            } else if (file.isFile()) {
                String pageName = fileName.substring(0, fileName.lastIndexOf("."));

                PageInfo pageInfo = new PageInfo();

                pageInfo.setPath(parentPath);
                pageInfo.setName(pageName);
                // TODO: fill other fields

                JsonInfo<? extends SiteNodeInfo> info = infoListLookup.get(pageName);
                if(info != null) {
                    pageInfo.setDescription(info.getDescription());
                    pageInfo.setDisplayName(info.getDisplayName());
                    pageInfo.setLocalizedName(info.getLocalizedName());

                    pageInfo.setTemplateReference(((JsonFileInfo) info).getTemplateReference());
                }
                else {
                    pageInfo.setTemplateReference(new TemplateReference());
                }

                pageInfo.setParent(containerInfo);

                infos.put(pageName, pageInfo);
            }
        }

        // sort entries corresponding to the previous stored list
        List<SiteNodeInfo> children = new ArrayList<>();
        List<JsonInfo<? extends SiteNodeInfo>> changedInfoList = new ArrayList<>();

        for (JsonInfo<? extends SiteNodeInfo> childInfo : infoList.getChildren()) {
            String fileName = childInfo.getName();
            SiteNodeInfo infoNode = infos.remove(fileName);
            changedInfoList.add(childInfo);

            if (infoNode == null) {
                // no file present anymore, skip it
                continue;
            }

            children.add(infoNode);
        }

        if (!infos.isEmpty()) {
            children.addAll(infos.values());
        }

        containerInfo.setNodes(children);

        if(changed) {
            for (SiteNodeInfo child : children) {
                if(child instanceof PageInfo) {
                    changedInfoList.add(new JsonFileInfo((PageInfo) child));
                }
                else if(child instanceof DirectoryInfo){
                    changedInfoList.add(new JsonDirectoryInfo((DirectoryInfo) child));
                }
                else {
                    throw new UnsupportedNodeTypeException(child.getClass());
                }
            }
            infoList.setChildren(changedInfoList);
            writeDirectoryInfoFile(dirInfoFile, infoList);
        }
    }

    /* private -> testing */
    SiteNodeContainerInfo getRoot() {
        return root;
    }

    //******* setters *******//

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setContentRoot(String contentRoot) {
        this.contentRoot = contentRoot;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }
}
