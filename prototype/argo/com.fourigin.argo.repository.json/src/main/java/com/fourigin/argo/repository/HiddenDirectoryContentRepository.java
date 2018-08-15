package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.InvalidSiteStructurePathException;
import com.fourigin.argo.models.UnsupportedNodeTypeException;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.path.SiteStructurePath;
import com.fourigin.argo.repository.model.JsonDirectoryInfo;
import com.fourigin.argo.repository.model.JsonFileInfo;
import com.fourigin.argo.repository.model.JsonInfo;
import com.fourigin.argo.repository.model.JsonInfoList;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("Duplicates")
public class HiddenDirectoryContentRepository implements ContentRepository {

    private static final String HIDDEN_DIRECTORY_NAME = ".cms";

    private static final String SITE_STRUCTURE_FILENAME = ".site";

    private static final String DIRECTORY_INFO_FILENAME = ".info";

    private static final String PAGE_INFO_FILE_POSTFIX = "_info";

    private final Logger logger = LoggerFactory.getLogger(HiddenDirectoryContentRepository.class);

    private ObjectMapper objectMapper;

    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    private String contentRoot;

    private SiteNodeContainerInfo root = null; // not initialized

    private PageInfoTraversingStrategy defaultTraversingStrategy;


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

        return resolveInfo(type, root, path);
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
        Objects.requireNonNull(parent, "Parent must not be null!");
        Objects.requireNonNull(path, "Path must not be null!");

        SiteStructurePath pathPointer = SiteStructurePath.forPath(path, parent);
        SiteNodeInfo result = pathPointer.getNodeInfo();

        if (result == null) {
            throw new InvalidSiteStructurePathException(path);
        }

        if (!type.isAssignableFrom(result.getClass())) {
            throw new InvalidSiteStructurePathException(path, type.getClass(), result.getClass());
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

        String parentPath = parent.getPath();

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

            JsonInfoList infoList = readDirectoryInfoFile(dirInfoFile);
            List<JsonInfo> oldChildren = infoList.getChildren();

            List<JsonInfo> newChildren = new ArrayList<>();
            for (JsonInfo child : oldChildren) {
                if (child.getName().equals(node.getName())) {
                    JsonInfo changedInfo = null;

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

        String parentPath = parent.getPath();

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

            children.remove(match);
            children.add(node);
            parent.setNodes(children);

            // write the dir-info file
            File dirInfoFile = getDirectoryInfoFile(hiddenDir);

            JsonInfoList infoList = readDirectoryInfoFile(dirInfoFile);
            List<JsonInfo> oldChildren = infoList.getChildren();

            List<JsonInfo> newChildren = new ArrayList<>();
            for (JsonInfo child : oldChildren) {
                if (child.getName().equals(node.getName())) {
                    JsonInfo changedInfo = null;

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

        // TODO implement me!

//        ReadWriteLock lock = getLock(path);
//        lock.writeLock().lock();
//
//        try {
//            // reload the content directory
//            File hiddenDir = getHiddenDirectory(path);
//            File contentDir = hiddenDir.getParentFile();
//            processContentDirectory(contentDir, parentPath, parent);
//
//            // add a new node
//            List<SiteNodeInfo> children = parent.getNodes();
//            if (children == null) {
//                children = new ArrayList<>();
//            }
//
//            String name = node.getName();
//            SiteNodeInfo match = null;
//            for (SiteNodeInfo child : children) {
//                if (name.equals(child.getName())) {
//                    match = child;
//                    break;
//                }
//            }
//
//            if(match == null) {
//                throw new IllegalStateException("No info node found for name '" + name + "'!");
//            }
//
//            children.remove(match);
//            children.add(node);
//            parent.setNodes(children);
//
//            // write the dir-info file
//            File dirInfoFile = getDirectoryInfoFile(hiddenDir);
//
//            JsonInfoList infoList = readDirectoryInfoFile(dirInfoFile);
//            List<JsonInfo> oldChildren = infoList.getChildren();
//
//            List<JsonInfo> newChildren = new ArrayList<>();
//            for (JsonInfo child : oldChildren) {
//                if (child.getName().equals(node.getName())) {
//                    JsonInfo changedInfo = null;
//
//                    if (node instanceof PageInfo) {
//                        changedInfo = new JsonFileInfo((PageInfo) node);
//                    } else if (node instanceof DirectoryInfo) {
//                        changedInfo = new JsonDirectoryInfo((DirectoryInfo) node);
//                    }
//                    newChildren.add(changedInfo);
//                } else {
//                    newChildren.add(child);
//                }
//            }
//            infoList.setChildren(newChildren);
//
//            writeDirectoryInfoFile(dirInfoFile, infoList);
//        }
//        finally {
//            lock.writeLock().unlock();
//        }
    }

    @Override
    public void deleteInfo(SiteNodeContainerInfo parent, String nodeName) {
        ensureInit();

        // TODO implement me!

    }

    //******* PAGE STATE methods *******//

    @Override
    public PageState resolvePageState(PageInfo pageInfo) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageInfoFile(hiddenDir, name);

        return readPageInfoFile(pageInfoFile);
    }

    @Override
    public void createPageState(PageInfo pageInfo, PageState pageState) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageInfoFile(hiddenDir, name);

        writePageInfoFile(pageInfoFile, pageState);
    }

    @Override
    public void updatePageState(PageInfo pageInfo, PageState pageState) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageInfoFile(hiddenDir, name);

        writePageInfoFile(pageInfoFile, pageState);
    }

    @Override
    public void deletePageState(PageInfo pageInfo, PageState pageState) {
        ensureInit();

        String name = pageInfo.getName();
        String path = pageInfo.getPath();

        File hiddenDir = getHiddenDirectory(path);
        File pageInfoFile = getPageInfoFile(hiddenDir, name);

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
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(PageInfo info, ContentPage contentPage) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        ensureInit();

        if (logger.isDebugEnabled())
            logger.debug("Updating ContentPage for parent '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentPageFile(info);
            if (!contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());

                return;
            }

            writeContentPage(info, contentPage, contentFile);
        }
        finally
        {
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
            if(!deleted){
                throw new IllegalStateException("Unable to delete content-file '" + contentFile.getAbsolutePath() + "'!");
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    //******* FLUSH methods *******//

    @Override
    public void flush() {
        initialize();
    }

    //******* private methods *******//

    private ReadWriteLock getLock(String id) {
        ReadWriteLock result = locks.get(id);
        if (result != null) {
            return result;
        }

        if (logger.isInfoEnabled())
            logger.info("Creating new lock for id '{}'", id);

        result = new ReentrantReadWriteLock();
        locks.putIfAbsent(id, result);
        result = locks.get(id); // to be sure it's really the correct lock...

        return result;
    }

    private File getSiteDescriptionFile() {
        File rootDirectory = new File(contentRoot);

        //noinspection ResultOfMethodCallIgnored
        rootDirectory.mkdirs();
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
        }

        File siteStructureFile = new File(rootDirectory, SITE_STRUCTURE_FILENAME);

        if (logger.isTraceEnabled())
            logger.trace("Resolved site-structure file '{}'", siteStructureFile.getAbsolutePath());

        return siteStructureFile;
    }

    private Map<String, String> readSiteStructureAttributes() {
        File structureFile = getSiteDescriptionFile();

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

    private void writeSiteStructureAttributes(Map<String, String> structure) {
        File structureFile = getSiteDescriptionFile();

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(structureFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, structure);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
        }
    }

    protected File getHiddenDirectory(String path) {
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
            if (logger.isDebugEnabled())
                logger.debug("Invalid path '{}', resolved to '{}'.", path, target.getAbsolutePath());
            return null;
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

        if (logger.isTraceEnabled())
            logger.trace("Resolved HIDDEN directory to '{}'", hiddenPath);

        return hiddenDir;
    }

    private File getDirectoryInfoFile(File hiddenDir) {
        return new File(hiddenDir, DIRECTORY_INFO_FILENAME);
    }

    private JsonInfoList readDirectoryInfoFile(File infoFile) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(infoFile))) {
            return objectMapper.readValue(is, JsonInfoList.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error reading info file ({})!", infoFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read info file (" + infoFile.getAbsolutePath() + ")", ex);
        }
    }

    private void writeDirectoryInfoFile(File infoFile, JsonInfoList infoList) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, infoList);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing info file (" + infoFile.getAbsolutePath() + ")!", ex);
        }
    }

    private File getPageInfoFile(File hiddenDir, String pageName) {
        String fileName = pageName + PAGE_INFO_FILE_POSTFIX;

        return new File(hiddenDir, fileName);
    }

    private PageState readPageInfoFile(File infoFile) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(infoFile))) {
            return objectMapper.readValue(is, PageState.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Error reading page info file ({})!", infoFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read page info file (" + infoFile.getAbsolutePath() + ")", ex);
        }
    }

    private void writePageInfoFile(File infoFile, PageState pageState) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, pageState);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing page info file (" + infoFile.getAbsolutePath() + ")!", ex);
        }
    }

    private File getContentRoot() {
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

        return rootDirectory;
    }

    private File getContentPageFile(PageInfo info) {
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

    private ContentPage readContentPage(PageInfo info, File contentFile) {
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

    private void writeContentPage(PageInfo info, ContentPage contentPage, File contentFile) {
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

    private void initialize() {
        this.root = null;

        ensureInit();
    }

    private void ensureInit() {
        if (root != null) {
            return;
        }

        File rootDir = getContentRoot();
        
        root = new DirectoryInfo();
        root.setPath("/");
        root.setParent(null);

        processContentDirectory(rootDir, "", root);
    }

    private void processContentDirectory(File dir, String parentPath, SiteNodeContainerInfo containerInfo) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            // empty directory, TODO
            return;
        }

        File hiddenDir = getHiddenDirectory(parentPath);
        File dirInfoFile = getDirectoryInfoFile(hiddenDir);

        JsonInfoList infoList;
        if (dirInfoFile.exists()) {
            infoList = readDirectoryInfoFile(dirInfoFile);
        } else {
            infoList = new JsonInfoList(parentPath);
            infoList.setChildren(new ArrayList<>());
        }

        Map<String, SiteNodeInfo> infos = new HashMap<>();

        for (File file : files) {
            String fileName = file.getName();

            if (fileName.startsWith(".")) {
                // skip hidden files
                continue;
            }

            String path = parentPath + '/' + fileName;

            if (file.isDirectory()) {
                DirectoryInfo dirInfo = new DirectoryInfo();

                dirInfo.setPath(path);
                // TODO: fill other fields

                processContentDirectory(file, path, dirInfo);

                infos.put(fileName, dirInfo);
            } else if (file.isFile()) {
                PageInfo pageInfo = new PageInfo();

                pageInfo.setPath(path);
                // TODO: fill other fields

                infos.put(fileName, pageInfo);
            }
        }

        // sort entries corresponding to the previous stored list
        List<SiteNodeInfo> children = new ArrayList<>();

        for (JsonInfo childInfo : infoList.getChildren()) {
            String fileName = childInfo.getName();
            SiteNodeInfo infoNode = infos.remove(fileName);

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
