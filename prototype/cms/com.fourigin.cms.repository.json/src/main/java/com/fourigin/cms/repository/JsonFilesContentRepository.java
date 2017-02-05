package com.fourigin.cms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.DirectoryInfo;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.cms.repository.strategies.PageInfoTraversingStrategy;
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
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonFilesContentRepository implements ContentRepository {
    private String contentRoot;

    private String siteStructureFileName;

    private String directoryInfoFileName;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ObjectMapper objectMapper;

    private DirectoryInfo root;

    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JsonFilesContentRepository.class);

    private static final String DEFAULT_SITE_DESCRIPTION_NAME = ".site";
    private static final String DEFAULT_DIRECTORY_INFO_NAME = ".info";

    class SiteStructureAttributes extends HashMap<String, String> {
        private static final long serialVersionUID = 3400161906589835280L;
    }

    public static class Builder {
        private String contentRoot;
        private String infoFilename;
        private String siteStructureFilename;
        private ObjectMapper objectMapper;
        private PageInfoTraversingStrategy traversingStrategy;

        public Builder contentRoot(String path){
            this.contentRoot = path;
            return this;
        }

        public Builder infoFilename(String infoFilename){
            this.infoFilename = infoFilename;
            return this;
        }

        public Builder siteStructureFilename(String siteStructureFilename){
            this.siteStructureFilename = siteStructureFilename;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper){
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder traversingStrategy(PageInfoTraversingStrategy traversingStrategy) {
            this.traversingStrategy = traversingStrategy;
            return this;
        }

        public JsonFilesContentRepository build(){
            JsonFilesContentRepository instance = new JsonFilesContentRepository();

            instance.setObjectMapper(objectMapper);
            instance.setContentRoot(contentRoot);
            instance.setDirectoryInfoFileName(infoFilename);
            instance.setSiteStructureFileName(siteStructureFilename);
            instance.setDefaultTraversingStrategy(traversingStrategy);

            return instance;
        }
    }

    //******* INFO methods *******//

    @Override
    public Map<String, String> resolveSiteAttributes() {
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
        ReadWriteLock lock = getLock("/");
        lock.writeLock().lock();

        try {
            writeSiteStructureAttributes(attributes);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path) {
        SiteNodeInfo node = selectInfo(root, path);
        if(type.isAssignableFrom(node.getClass())){
            return type.cast(node);
        }

        return null;
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
        SiteNodeInfo node = selectInfo(parent, path);
        if(type.isAssignableFrom(node.getClass())){
            return type.cast(node);
        }

        return null;
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path) {
        if(defaultTraversingStrategy == null){
            throw new IllegalStateException("Unable to resolve iterator! No default iteration strategy is defined!");
        }

        return resolveInfos(root, path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy) {
        Objects.requireNonNull(path, "Path must not be null!");

        return resolveInfos(root, path, traversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path) {
        Objects.requireNonNull(path, "Path must not be null!");

        if(defaultTraversingStrategy == null){
            throw new IllegalStateException("Unable to resolve iterator! No default iteration strategy is defined!");
        }

        return resolveInfos(parent, path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy) {
        Objects.requireNonNull(path, "Path must not be null!");
        Objects.requireNonNull(traversingStrategy, "Iteration strategy must not be null!");

        SiteNodeInfo nodeInfo = selectInfo(parent, path);
        if(SiteNodeContainerInfo.class.isAssignableFrom(nodeInfo.getClass())){
            SiteNodeContainerInfo containerInfo = SiteNodeContainerInfo.class.cast(nodeInfo);
            return traversingStrategy.collect(containerInfo);
        }

        return Collections.singletonList(PageInfo.class.cast(nodeInfo));
    }

    @Override
    public void createInfo(String path, SiteNodeInfo info) {
        SiteNodeInfo parent = selectInfo(root, path);
        if(!DirectoryInfo.class.isAssignableFrom(parent.getClass())){
            throw new IllegalArgumentException("Path '" + path + "' is not a directory! Unable to create a sub node!");
        }

        DirectoryInfo parentDir = DirectoryInfo.class.cast(parent);
        createInfo(parentDir, info);
    }

    @Override
    public void createInfo(SiteNodeContainerInfo parent, SiteNodeInfo info) {
        List<SiteNodeInfo> children = parent.getNodes();
        if(children == null){
            children = new ArrayList<>();
        }

        String name = info.getName();
        for (SiteNodeInfo child : children) {
            if(name.equals(child.getName())){
                throw new DuplicateSiteNodeFoundException(parent.getPath(), name);
            }
        }

        children.add(info);
        parent.setNodes(children);
    }

    @Override
    public void updateInfo(String path, SiteNodeInfo info) {
        SiteNodeInfo parent = selectInfo(root, path);
        if(!DirectoryInfo.class.isAssignableFrom(parent.getClass())){
            throw new IllegalArgumentException("Path '" + path + "' is not a directory! Unable to update a sub node!");
        }

        DirectoryInfo parentDir = DirectoryInfo.class.cast(parent);
        updateInfo(parentDir, info);
    }

    @Override
    public void updateInfo(SiteNodeContainerInfo parent, SiteNodeInfo info) {
        List<SiteNodeInfo> children = parent.getNodes();
        if(children == null || children.isEmpty()){
            throw new SiteNodeNotFoundException(info.getName(), info.getPath());
        }

        String name = info.getName();
        SiteNodeInfo existingNode = null;
        for (SiteNodeInfo child : children) {
            if(name.equals(child.getName())){
                existingNode = child;
                break;
            }
        }
        if(existingNode == null){
            throw new SiteNodeNotFoundException(info.getName(), info.getPath());
        }

        children.remove(existingNode);
        children.add(info);
        parent.setNodes(children);
    }

    @Override
    public void deleteInfo(String path) {
        SiteNodeInfo info = selectInfo(root, path);
        if(info == null){
            throw new SiteNodeNotFoundException(path, "/");
        }

        deleteInfo(info.getParent(), info.getName());
    }

    @Override
    public void deleteInfo(SiteNodeContainerInfo parent, String path) {
        List<SiteNodeInfo> children = parent.getNodes();
        if(children == null || children.isEmpty()){
            throw new SiteNodeNotFoundException(path, parent.getPath());
        }

        SiteNodeInfo existingNode = null;
        for (SiteNodeInfo child : children) {
            if(path.equals(child.getName())){
                existingNode = child;
                break;
            }
        }
        if(existingNode == null){
            throw new SiteNodeNotFoundException(path, parent.getPath());
        }

        children.remove(existingNode);
        parent.setNodes(children);
    }

    //******* PAGE methods *******//

    @Override
    public ContentPage retrieve(PageInfo info) {
        Objects.requireNonNull(info, "PageInfo must not be null!");

        if (logger.isDebugEnabled())
            logger.debug("Retrieving ContentPage for info '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.readLock().lock();

        try {
            File contentFile = getContentFile(info);
            String fullPath = contentFile.getAbsolutePath();
            if (!contentFile.exists()) {
                if (logger.isInfoEnabled())
                    logger.info("Content file {} does not exist!", fullPath);

                return null;
            }

            return readContentPage(info, contentFile);
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    @Override
    public void create(PageInfo info, ContentPage contentPage) {
        Objects.requireNonNull(info, "PageInfo must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        if (logger.isDebugEnabled())
            logger.debug("Creating ContentPage for '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(info);
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

        if (logger.isDebugEnabled())
            logger.debug("Updating ContentPage for parent '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(info);
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

        if (logger.isDebugEnabled())
            logger.debug("Deleting ContentPage for parent '{}'", info);

        ReadWriteLock lock = getLock(info.getReference());
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(info);
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

    //******* private methods *******//

    private void initialize(){
        // TODO: read the structure
    }

    private SiteNodeInfo selectInfo(SiteNodeContainerInfo base, String path){
        if(root == null){
            initialize();
        }

        // clean path
        path = path.replace("//", "/").trim();

        SiteNodeInfo current = SiteNodeInfo.class.cast(base);
        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();

            if(!(SiteNodeContainerInfo.class.isAssignableFrom(current.getClass()))){
                throw new MissingSiteDirectoryException(token, current);
            }

            List<SiteNodeInfo> children = ((SiteNodeContainerInfo) current).getNodes();
            if(children == null || children.isEmpty()){
                throw new UnresolvableSiteStructurePathException(token, path);
            }

            SiteNodeInfo found = null;
            for (SiteNodeInfo child : children) {
                if(token.equals(child.getName())){
                    found = child;
                    break;
                }
            }
            if(found == null){
                throw new UnresolvableSiteStructurePathException(token, path);
            }

            current = found;
        }

        return current;
    }

    private File getInfoFile(String parentPath) {
        File rootDirectory = new File(contentRoot);

        //noinspection ResultOfMethodCallIgnored
        rootDirectory.mkdirs();
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
        }

        String filename = directoryInfoFileName;
        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_DIRECTORY_INFO_NAME;
        }

        // remove first '/'
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        // replace all '/' with '_'
        filename = filename.replace('/', '_');

        File directory = new File(rootDirectory, parentPath);

        File infoFile = new File(directory, filename);

        if (logger.isTraceEnabled()) logger.trace("Resolved info file '{}'", infoFile.getAbsolutePath());

        return infoFile;
    }

    private DirectoryInfo readInfo(String path) {
        File infoFile = getInfoFile(path);

        InputStream os = null;
        try {
            os = new BufferedInputStream(new FileInputStream(infoFile));
            return objectMapper.readValue(os, DirectoryInfo.class);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error reading directory info (" + infoFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private void writeInfo(PageInfo info) {
        File infoFile = getInfoFile(info.getParent().getPath());

        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(infoFile));
            objectMapper.writeValue(os, info);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing directory info (" + infoFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private Map<String, String> readSiteStructureAttributes() {
        File structureFile = getSiteDescriptionFile();

        InputStream os = null;
        try {
            os = new BufferedInputStream(new FileInputStream(structureFile));
            return objectMapper.readValue(os, SiteStructureAttributes.class);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error reading site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private void writeSiteStructureAttributes(Map<String, String> structure) {
        File structureFile = getSiteDescriptionFile();

        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(structureFile));
            objectMapper.writeValue(os, structure);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private File getSiteDescriptionFile() {
        File rootDirectory = new File(contentRoot);

        //noinspection ResultOfMethodCallIgnored
        rootDirectory.mkdirs();
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
        }

        String filename = siteStructureFileName;
        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_SITE_DESCRIPTION_NAME;
        }

        // remove first '/'
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        // replace all '/' with '_'
        filename = filename.replace('/', '_');

        File siteStructureFile = new File(rootDirectory, filename);

        if (logger.isTraceEnabled())
            logger.trace("Resolved site-structure file '{}'", siteStructureFile.getAbsolutePath());

        return siteStructureFile;
    }

    private ContentPage readContentPage(PageInfo info, File contentFile){
        InputStream is = null;
        ContentPage result = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(contentFile));
            result = objectMapper.readValue(is, ContentPage.class);
        }
        catch(IOException ex)
        {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error loading content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

        if(result == null)
        {
            if(logger.isWarnEnabled())
                logger.warn("Error reading content file '{}'", contentFile.getAbsolutePath());

            return null;
        }

        result.setId(info.getName()); // why?

        return result;
    }

    private void writeContentPage(PageInfo info, ContentPage contentPage, File contentFile){
        OutputStream os = null;
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(contentFile));
            objectMapper.writeValue(os, contentPage);
        }
        catch(IOException ex)
        {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }
    }

    private File getContentFile(PageInfo info) {
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

        if(logger.isTraceEnabled()) logger.trace("Resolved content file '{}'", contentFile.getAbsolutePath());

        return contentFile;
    }

    private ReadWriteLock getLock(String id) {
        ReadWriteLock result = locks.get(id);
        if(result != null){
            return result;
        }

        if (logger.isInfoEnabled())
            logger.info("Creating new lock for id '{}'", id);

        result = new ReentrantReadWriteLock();
        locks.putIfAbsent(id, result);
        result = locks.get(id); // to be sure it's really the correct lock...

        return result;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setContentRoot(String contentRoot) {
        this.contentRoot = contentRoot;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }

    public void setSiteStructureFileName(String siteStructureFileName) {
        this.siteStructureFileName = siteStructureFileName;
    }

    public void setDirectoryInfoFileName(String directoryInfoFileName) {
        this.directoryInfoFileName = directoryInfoFileName;
    }


}
