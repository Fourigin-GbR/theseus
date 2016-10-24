//package com.fourigin.cms.repository;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fourigin.cms.models.structure.nodes.DirectoryInfo;
//import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
//import com.fourigin.cms.models.structure.nodes.SiteNodeInfoContainer;
//import com.fourigin.cms.models.structure.nodes.PageInfo;
//import com.fourigin.cms.repository.strategies.TraversingStrategy;
//import org.apache.commons.io.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.StringTokenizer;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public class JsonHiddenFilesSiteStructureRepository implements SiteStructureRepository {
//    private String contentRoot;
//
//    private String siteDescriptionName;
//
//    private String directoryInfoName;
//
//    private DirectoryInfo root;
//
//    private ObjectMapper objectMapper;
//
//    private TraversingStrategy<PageInfo, SiteNodeInfo> defaultIterationStrategy;
//
//    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
//
//    private static final String DEFAULT_SITE_DESCRIPTION_NAME = ".site";
//    private static final String DEFAULT_DIRECTORY_INFO_NAME = ".info";
//
//    private final Logger logger = LoggerFactory.getLogger(JsonHiddenFilesSiteStructureRepository.class);
//
//    @Override
//    public void createNode(String path, SiteNodeInfo node) {
//        ReadWriteLock lock = getLock(path + "/" + node.getName());
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void createNode(SiteNodeInfoContainer parent, SiteNodeInfo node) {
//        ReadWriteLock lock = getLock(parent.getPath() + "/" + node.getName());
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void updateNode(String path, SiteNodeInfo node) {
//        ReadWriteLock lock = getLock(path + "/" + node.getName());
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void updateNode(SiteNodeInfoContainer parent, SiteNodeInfo node) {
//        ReadWriteLock lock = getLock(parent.getPath() + "/" + node.getName());
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void deleteNode(String path) {
//        ReadWriteLock lock = getLock(path);
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void deleteNode(SiteNodeInfoContainer parent, String path) {
//        ReadWriteLock lock = getLock(parent.getPath() + "/" + path);
//        lock.writeLock().lock();
//
//        try {
//
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public Map<String, String> resolveSiteAttributes() {
//        ReadWriteLock lock = getLock("/");
//        lock.readLock().lock();
//
//        try {
//            return readSiteStructureAttributes();
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public void updateSiteStructureAttributes(Map<String, String> attributes) {
//        ReadWriteLock lock = getLock("/");
//        lock.writeLock().lock();
//
//        try {
//            writeSiteStructureAttributes(attributes);
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    private SiteNodeInfo select(SiteNodeInfoContainer base, String path){
//        if(root == null){
//            initialize();
//        }
//
//        // clean path
//        path = path.replace("//", "/").trim();
//
//        SiteNodeInfo current = SiteNodeInfo.class.cast(base);
//        StringTokenizer tok = new StringTokenizer(path, "/");
//        while (tok.hasMoreTokens()) {
//            String token = tok.nextToken().trim();
//
//            if(!(current instanceof SiteNodeInfoContainer)){
//                throw new MissingSiteDirectoryException(token, current);
//            }
//
//            List<SiteNodeInfo> children = ((SiteNodeInfoContainer) current).getNodes();
//            if(children == null || children.isEmpty()){
//                throw new UnresolvableSiteStructurePathException(token, path);
//            }
//
//            SiteNodeInfo found = null;
//            for (SiteNodeInfo child : children) {
//                if(token.equals(child.getName())){
//                    found = child;
//                    break;
//                }
//            }
//            if(found == null){
//                throw new UnresolvableSiteStructurePathException(token, path);
//            }
//
//            current = found;
//        }
//
//        return current;
//    }
//
//    private void initialize(){
//        // TODO: read the structure
//    }
//
//    private <T extends SiteNodeInfo> T convert(Class<T> clazz, DirectoryInfoItem item, String path) {
//        if (clazz.isAssignableFrom(PageInfo.class)) {
//            PageInfo page = new PageInfo();
//            page.setName(item.getName());
//            page.setPath(path);
//            page.setLocalizedName(item.getLocalizedName());
//            page.setDisplayName(item.getDisplayName());
//            page.setDescription(item.getDescription());
//            page.setStaged(item.isStaged());
//            page.setCompileState(item.getCompileState());
//            page.setChecksum(item.getChecksum(), item.getDataSourceChecksum());
//            return clazz.cast(page);
//        }
//
//        if (clazz.isAssignableFrom(DirectoryInfo.class)) {
//            DirectoryInfo directory = new DirectoryInfo();
//            directory.setName(item.getName());
//            directory.setPath(path);
//            directory.setLocalizedName(item.getLocalizedName());
//            directory.setDisplayName(item.getDisplayName());
//            directory.setDescription(item.getDescription());
//            return clazz.cast(directory);
//        }
//
//        throw new IllegalArgumentException("Unsupported class '" + clazz.getName() + "'!");
//    }
//
//    @Override
//    public <T extends SiteNodeInfo> T resolveNode(Class<T> type, String path) {
//        SiteNodeInfo node = select(root, path);
//        if(type.isAssignableFrom(node.getClass())){
//            return type.cast(node);
//        }
//
//        return null;
//    }
//
//    @Override
//    public <T extends SiteNodeInfo> T resolveNode(Class<T> type, SiteNodeInfoContainer parent, String path) {
//        SiteNodeInfo node = select(parent, path);
//        if(type.isAssignableFrom(node.getClass())){
//            return type.cast(node);
//        }
//
//        return null;
//    }
//
//    @Override
//    public Collection<PageInfo> resolveNodes(String path) {
//        if(defaultIterationStrategy == null){
//            throw new IllegalStateException("Unable to resolve iterator! No default iteration strategy is defined!");
//        }
//
//        return resolveNodes(root, path, defaultIterationStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveNodes(String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy) {
//        Objects.requireNonNull(path, "Path must not be null!");
//
//        return resolveNodes(root, path, iterationStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveNodes(SiteNodeInfoContainer parent, String path) {
//        Objects.requireNonNull(path, "Path must not be null!");
//
//        if(defaultIterationStrategy == null){
//            throw new IllegalStateException("Unable to resolve iterator! No default iteration strategy is defined!");
//        }
//
//        return resolveNodes(parent, path, defaultIterationStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveNodes(SiteNodeInfoContainer parent, String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy) {
//        Objects.requireNonNull(path, "Path must not be null!");
//        Objects.requireNonNull(iterationStrategy, "Iteration strategy must not be null!");
//
//        SiteNodeInfo base = select(parent, path);
//
//        return iterationStrategy.collect(base);
//    }
//
//    private ReadWriteLock getLock(String id) {
//        ReadWriteLock result = locks.get(id);
//        if (result != null) {
//            return result;
//        }
//
//        if (logger.isInfoEnabled())
//            logger.info("Creating new lock for id '{}'", id);
//
//        result = new ReentrantReadWriteLock();
//        locks.putIfAbsent(id, result);
//        result = locks.get(id); // to be sure it's really the correct lock...
//
//        return result;
//    }
//
//    private DirectoryInfo readInfo(String path) {
//        File infoFile = getInfoFile(path);
//
//        InputStream os = null;
//        try {
//            os = new BufferedInputStream(new FileInputStream(infoFile));
//            return objectMapper.readValue(os, com.fourigin.cms.repository.DirectoryInfo.class);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error reading directory info (" + infoFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            IOUtils.closeQuietly(os);
//        }
//    }
//
//    private void writeInfo(DirectoryInfo info) {
//        File infoFile = getInfoFile(info.getParentPath());
//
//        OutputStream os = null;
//        try {
//            os = new BufferedOutputStream(new FileOutputStream(infoFile));
//            objectMapper.writeValue(os, info);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing directory info (" + infoFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            IOUtils.closeQuietly(os);
//        }
//    }
//
//    private File getRootDirectory() {
//        File root = new File(contentRoot);
//        if (!root.exists()) {
//            if (!root.mkdirs()) {
//                throw new IllegalStateException("Unable to create root directory '" + root.getAbsolutePath() + "'!");
//            }
//        }
//
//        return root;
//    }
//
//    private Map<String, String> readSiteStructureAttributes() {
//        File structureFile = getSiteDescriptionFile();
//
//        InputStream os = null;
//        try {
//            os = new BufferedInputStream(new FileInputStream(structureFile));
//            return objectMapper.readValue(os, SiteStructureAttributes.class);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error reading site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            IOUtils.closeQuietly(os);
//        }
//    }
//
//    private void writeSiteStructureAttributes(Map<String, String> structure) {
//        File structureFile = getSiteDescriptionFile();
//
//        OutputStream os = null;
//        try {
//            os = new BufferedOutputStream(new FileOutputStream(structureFile));
//            objectMapper.writeValue(os, structure);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            IOUtils.closeQuietly(os);
//        }
//    }
//
//    private File getSiteDescriptionFile() {
//        File rootDirectory = new File(contentRoot);
//
//        //noinspection ResultOfMethodCallIgnored
//        rootDirectory.mkdirs();
//        if (!rootDirectory.isDirectory()) {
//            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
//        }
//
//        String filename = siteDescriptionName;
//        if (filename == null || filename.isEmpty()) {
//            filename = DEFAULT_SITE_DESCRIPTION_NAME;
//        }
//
//        // remove first '/'
//        if (filename.startsWith("/")) {
//            filename = filename.substring(1);
//        }
//
//        // replace all '/' with '_'
//        filename = filename.replace('/', '_');
//
//        File siteStructureFile = new File(rootDirectory, filename);
//
//        if (logger.isTraceEnabled())
//            logger.trace("Resolved site-structure file '{}'", siteStructureFile.getAbsolutePath());
//
//        return siteStructureFile;
//    }
//
//    private File getInfoFile(String parentPath) {
//        File rootDirectory = new File(contentRoot);
//
//        //noinspection ResultOfMethodCallIgnored
//        rootDirectory.mkdirs();
//        if (!rootDirectory.isDirectory()) {
//            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
//        }
//
//        String filename = directoryInfoName;
//        if (filename == null || filename.isEmpty()) {
//            filename = DEFAULT_DIRECTORY_INFO_NAME;
//        }
//
//        // remove first '/'
//        if (filename.startsWith("/")) {
//            filename = filename.substring(1);
//        }
//
//        // replace all '/' with '_'
//        filename = filename.replace('/', '_');
//
//        File directory = new File(rootDirectory, parentPath);
//
//        File infoFile = new File(directory, filename);
//
//        if (logger.isTraceEnabled()) logger.trace("Resolved info file '{}'", infoFile.getAbsolutePath());
//
//        return infoFile;
//    }
//
//    class SiteStructureAttributes extends HashMap<String, String> {
//    }
//
//    public void setContentRoot(String contentRoot) {
//        this.contentRoot = contentRoot;
//    }
//
//    public void setSiteStructureFileName(String siteDescriptionName) {
//        this.siteDescriptionName = siteDescriptionName;
//    }
//
//    public void setDirectoryInfoFileName(String directoryInfoName) {
//        this.directoryInfoName = directoryInfoName;
//    }
//
//    public void setRoot(DirectoryInfo root) {
//        this.root = root;
//    }
//
//    public void setObjectMapper(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    public void setDefaultTraversingStrategy(TraversingStrategy<PageInfo, SiteNodeInfo> defaultIterationStrategy) {
//        this.defaultIterationStrategy = defaultIterationStrategy;
//    }
//}