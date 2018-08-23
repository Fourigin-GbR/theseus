//package com.fourigin.argo.repository;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fourigin.argo.models.content.ContentPage;
//import com.fourigin.argo.models.structure.CompileState;
//import com.fourigin.argo.models.structure.nodes.DirectoryInfo;
//import com.fourigin.argo.models.structure.nodes.PageInfo;
//import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
//import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
//import com.fourigin.argo.models.template.TemplateReference;
//import com.fourigin.argo.repository.model.JsonDirectoryInfo;
//import com.fourigin.argo.repository.model.JsonFileInfo;
//import com.fourigin.argo.repository.model.JsonInfo;
//import com.fourigin.argo.repository.model.JsonInfoList;
//import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
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
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.StringTokenizer;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public class JsonFilesContentRepository implements ContentRepository {
//    private String contentRoot;
//
//    private String siteStructureFileName;
//
//    private String directoryInfoFileName;
//
//    private PageInfoTraversingStrategy defaultTraversingStrategy;
//
//    private ObjectMapper objectMapper;
//
////    private SiteNodeContainerInfo root;
//
//    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
//
//    private final Logger logger = LoggerFactory.getLogger(JsonFilesContentRepository.class);
//
//    private static final String DEFAULT_SITE_DESCRIPTION_NAME = ".site";
//    private static final String DEFAULT_DIRECTORY_INFO_NAME = ".info";
//
//    public void flush(){
//        //root = null;
//    }
//
//    //******* INFO methods *******//
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
//    @Override
//    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        return resolveInfo(type, root, path);
//    }
//
//    @Override
//    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
//        if(logger.isDebugEnabled())
//            logger.debug("info tree:\n{}", buildInfoTree(parent, 0, false));
//
//        SiteNodeInfo node = selectInfo(parent, path);
//        if(!type.isAssignableFrom(node.getClass())){
//            return null;
//        }
//
//        return type.cast(node);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveInfos(String path) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        return resolveInfos(root, path, defaultTraversingStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        return resolveInfos(root, path, traversingStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path) {
//        return resolveInfos(parent, path, defaultTraversingStrategy);
//    }
//
//    @Override
//    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy) {
//        Objects.requireNonNull(path, "Path must not be null!");
//        Objects.requireNonNull(traversingStrategy, "Traversing strategy must not be null!");
//
//        SiteNodeInfo nodeInfo = selectInfo(parent, path);
//        if(SiteNodeContainerInfo.class.isAssignableFrom(nodeInfo.getClass())){
//            SiteNodeContainerInfo containerInfo = SiteNodeContainerInfo.class.cast(nodeInfo);
//            return traversingStrategy.collect(containerInfo);
//        }
//
//        return Collections.singletonList(PageInfo.class.cast(nodeInfo));
//    }
//
//    @Override
//    public void createInfo(String path, SiteNodeInfo info) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        SiteNodeInfo parent = selectInfo(root, path);
//        if(!DirectoryInfo.class.isAssignableFrom(parent.getClass())){
//            throw new IllegalArgumentException("'" + path + "' is not a directory! Unable to create a sub node!");
//        }
//
//        DirectoryInfo parentDir = DirectoryInfo.class.cast(parent);
//        createInfo(parentDir, info);
//    }
//
//    @Override
//    public void createInfo(SiteNodeContainerInfo parent, SiteNodeInfo info) {
//        List<SiteNodeInfo> children = parent.getNodes();
//        if(children == null){
//            children = new ArrayList<>();
//        }
//
//        String name = info.getName();
//        for (SiteNodeInfo child : children) {
//            if(name.equals(child.getName())){
//                throw new DuplicateSiteNodeFoundException(parent.getPath(), name);
//            }
//        }
//
//        children.add(info);
//        parent.setNodes(children);
//    }
//
//    @Override
//    public void updateInfo(String path, SiteNodeInfo info) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        SiteNodeInfo parent = selectInfo(root, path);
//        if(!DirectoryInfo.class.isAssignableFrom(parent.getClass())){
//            throw new IllegalArgumentException("'" + path + "' is not a directory! Unable to update a sub node!");
//        }
//
//        DirectoryInfo parentDir = DirectoryInfo.class.cast(parent);
//        updateInfo(parentDir, info);
//
//        // write info file (test it!)
//        JsonInfoList infoList = readJsonInfoList(path);
//        List<JsonInfo> newChildren = new ArrayList<>();
//        List<JsonInfo> children = infoList.getChildren();
//        for (JsonInfo child : children) {
//            if(child.getName().equals(info.getName())) {
//                JsonInfo changedInfo = null;
//                if(info instanceof PageInfo) {
//                    changedInfo = new JsonFileInfo((PageInfo) info);
//                }
//                else if(info instanceof DirectoryInfo){
//                    changedInfo = new JsonDirectoryInfo((DirectoryInfo) info);
//                }
//                newChildren.add(changedInfo);
//            }
//            else {
//                newChildren.add(child);
//            }
//        }
//        infoList.setChildren(newChildren);
//        writeJsonInfoList(path, infoList);
//
//    }
//
//    @Override
//    public void updateInfo(SiteNodeContainerInfo parent, SiteNodeInfo info) {
//        if (logger.isDebugEnabled()) logger.debug("Updating info for parent '{}': {}", parent.getPath(), info);
//
//        List<SiteNodeInfo> children = parent.getNodes();
//        if(children == null || children.isEmpty()){
//            throw new SiteNodeNotFoundException(info.getName(), info.getPath());
//        }
//
//        String name = info.getName();
//        SiteNodeInfo existingNode = null;
//        for (SiteNodeInfo child : children) {
//            if (logger.isDebugEnabled()) logger.debug("Comparing child '{}' with requested node '{}'.", child.getName(), name);
//            if(name.equals(child.getName())){
//                existingNode = child;
//                break;
//            }
//        }
//        if(existingNode == null){
//            throw new SiteNodeNotFoundException(info.getName(), info.getPath());
//        }
//
//        children.remove(existingNode);
//        children.add(info);
//        parent.setNodes(children);
//    }
//
//    @Override
//    public void deleteInfo(String path) {
////        if(root == null){
////            initialize();
////        }
//
//        SiteNodeContainerInfo root = initialize();
//
//        SiteNodeInfo info = selectInfo(root, path);
//        if(info == null){
//            throw new SiteNodeNotFoundException(path, "/");
//        }
//
//        // TODO: navigate to parent first!
////        deleteInfo(info.getParent(), info.getName());
//    }
//
//    @Override
//    public void deleteInfo(SiteNodeContainerInfo parent, String path) {
//        List<SiteNodeInfo> children = parent.getNodes();
//        if(children == null || children.isEmpty()){
//            throw new SiteNodeNotFoundException(path, parent.getPath());
//        }
//
//        SiteNodeInfo existingNode = null;
//        for (SiteNodeInfo child : children) {
//            if(path.equals(child.getName())){
//                existingNode = child;
//                break;
//            }
//        }
//        if(existingNode == null){
//            throw new SiteNodeNotFoundException(path, parent.getPath());
//        }
//
//        children.remove(existingNode);
//        parent.setNodes(children);
//    }
//
//    //******* PAGE methods *******//
//
//    @Override
//    public ContentPage retrieve(PageInfo info) {
//        Objects.requireNonNull(info, "PageInfo must not be null!");
//
//        if (logger.isDebugEnabled())
//            logger.debug("Retrieving ContentPage for info '{}'", info);
//
//        ReadWriteLock lock = getLock(info.getReference());
//        lock.readLock().lock();
//
//        try {
//            File contentFile = getContentFile(info);
//            String fullPath = contentFile.getAbsolutePath();
//            if (!contentFile.exists()) {
//                if (logger.isInfoEnabled())
//                    logger.info("Content file {} does not exist!", fullPath);
//
//                return null;
//            }
//
//            return readContentPage(info, contentFile);
//        }
//        finally
//        {
//            lock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public void create(PageInfo info, ContentPage contentPage) {
//        Objects.requireNonNull(info, "PageInfo must not be null!");
//        Objects.requireNonNull(contentPage, "Content-page must not be null!");
//
//        if (logger.isDebugEnabled())
//            logger.debug("Creating ContentPage for '{}'", info);
//
//        ReadWriteLock lock = getLock(info.getReference());
//        lock.writeLock().lock();
//
//        try {
//            File contentFile = getContentFile(info);
//            if (contentFile.exists()) {
//                if (logger.isErrorEnabled())
//                    logger.error("Content file {} does already exist!", contentFile.getAbsolutePath());
//
//                return;
//            }
//
//            writeContentPage(info, contentPage, contentFile);
//        }
//        finally
//        {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void update(PageInfo info, ContentPage contentPage) {
//        Objects.requireNonNull(info, "PageInfo must not be null!");
//        Objects.requireNonNull(contentPage, "Content-page must not be null!");
//
//        if (logger.isDebugEnabled())
//            logger.debug("Updating ContentPage for parent '{}'", info);
//
//        ReadWriteLock lock = getLock(info.getReference());
//        lock.writeLock().lock();
//
//        try {
//            File contentFile = getContentFile(info);
//            if (!contentFile.exists()) {
//                if (logger.isErrorEnabled())
//                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());
//
//                return;
//            }
//
//            writeContentPage(info, contentPage, contentFile);
//        }
//        finally
//        {
//            lock.writeLock().unlock();
//        }
//    }
//
//    @Override
//    public void delete(PageInfo info) {
//        Objects.requireNonNull(info, "PageInfo must not be null!");
//
//        if (logger.isDebugEnabled())
//            logger.debug("Deleting ContentPage for parent '{}'", info);
//
//        ReadWriteLock lock = getLock(info.getReference());
//        lock.writeLock().lock();
//
//        try {
//            File contentFile = getContentFile(info);
//            if (!contentFile.exists()) {
//                if (logger.isErrorEnabled())
//                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());
//
//                return;
//            }
//
//            boolean deleted = contentFile.delete();
//            if(!deleted){
//                throw new IllegalStateException("Unable to delete content-file '" + contentFile.getAbsolutePath() + "'!");
//            }
//        }
//        finally
//        {
//            lock.writeLock().unlock();
//        }
//    }
//
//    //******* private methods *******//
//
//    public static String buildInfoTree(SiteNodeContainerInfo container, int indent, boolean emptyIndent){
//        StringBuilder builder = new StringBuilder();
//
//        if(indent == 0){
//            builder.append("/ (").append(container).append(")\n");
//        }
//
//        String indentString = getIndentString(indent, emptyIndent);
//
//        List<SiteNodeInfo> nodes = container.getNodes();
//        if(nodes != null && !nodes.isEmpty()){
//            int count = 0;
//            for (SiteNodeInfo node : nodes) {
//                count++;
//
//                builder.append(indentString);
//                builder.append("+--").append(node.getName()).append('\n');
//
//                if (node instanceof DirectoryInfo) {
//                    builder.append(buildInfoTree((SiteNodeContainerInfo) node, indent + 1, count == nodes.size()));
//                }
//            }
//
//            builder.append(indentString).append('\n');
//        }
//
//        return builder.toString();
//    }
//
//    private static String getIndentString(int indent, boolean empty) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < indent; i++) {
//            if(i == indent-1 && empty){
//                sb.append("   ");
//            }
//            else {
//                sb.append("|  ");
//            }
//        }
//        return sb.toString();
//    }
//
//    private SiteNodeContainerInfo initialize(){
////        if(root != null){
////            if (logger.isDebugEnabled()) logger.debug("Repository is already initialized for '{}'.", contentRoot);
////            return;
////        }
//
//        SiteNodeContainerInfo result;
//
//        if (logger.isDebugEnabled()) logger.debug("Initializing content repository for '{}'.", contentRoot);
//        try {
//            File dir = new File(contentRoot);
//
//            String rootPath = "/";
//
//            boolean changed = false;
//            JsonInfoList rootList = readJsonInfoList(rootPath);
//            if (rootList == null) {
//                rootList = new JsonInfoList("/");
//                changed = true;
//            }
//
//            result = createNewDirectoryInfo("", rootPath);
//
//            List<SiteNodeInfo> nodes = parseDirectory(dir, rootPath, result, rootList);
//
//            result.setNodes(nodes);
//
//            if (changed) {
//                writeJsonInfoList(rootPath, rootList);
//            }
//        }
//        catch(Throwable th){
//            if (logger.isErrorEnabled()) logger.error("Error occurred while loading content structure, resetting the repository.");
////            root = null;
//            throw th;
//        }
//
//        return result;
//    }
//
//    private static String normalizeFileName(String name){
//        if(name == null){
//            return null;
//        }
//
//        if(name.endsWith(".json")){
//            return name.substring(0, name.length() - ".json".length()).trim();
//        }
//
//        return name.trim();
//    }
//
//    private DirectoryInfo createNewDirectoryInfo(String name, String localPath) {
//        DirectoryInfo info = new DirectoryInfo();
//
//        name = normalizeFileName(name);
//
//        info.setName(name);
//        info.setLocalizedName(name);
//        info.setPath(localPath);
//        info.setDescription("Auto-created directory info. Don't forget to specify all properties!");
//        info.setDisplayName(name);
//
//        info.setNodes(null);
//
//        return info;
//    }
//
//    private PageInfo createNewPageInfo(String name, String localPath) {
//        PageInfo info = new PageInfo();
//
//        name = normalizeFileName(name);
//
//        info.setName(name);
//        info.setLocalizedName(name);
//        info.setPath(localPath);
//        info.setDescription("Auto-created directory info. Don't forget to specify all properties!");
//        info.setDisplayName(name);
//        info.setStaged(false);
//
//        TemplateReference templateReference = new TemplateReference();
//        templateReference.setTemplateId("");
//        templateReference.setVariationId("");
//        templateReference.setRevision("");
//        info.setTemplateReference(templateReference);
//
//        CompileState compileState = new CompileState();
//        compileState.setChecksum("");
//        compileState.setCompiled(false);
//        compileState.setMessage("");
//        compileState.setTimestamp(new Date().getTime());
//        info.setCompileState(compileState);
//
//        File contentFile = getContentFile(info);
//        ContentPage contentPage = readContentPage(info, contentFile);
//
//        info.buildChecksum(contentPage);
//
//        return info;
//    }
//
//    private List<SiteNodeInfo> parseDirectory(File dir, String localPath, SiteNodeContainerInfo parent, JsonInfoList jsonInfos) {
//        if(dir == null){
//            if (logger.isErrorEnabled()) logger.error("Unable to parse null-directory!");
//            return null;
//        }
//
//        if(jsonInfos == null){
//            if (logger.isErrorEnabled()) logger.error("Unable to parse a directory without a list of JsonInfo!");
//            return null;
//        }
//
//        if(!dir.isDirectory()){
//            if (logger.isErrorEnabled()) logger.error("Unable to parse '{}', because it's not a directory!", dir.getAbsolutePath());
//            return null;
//        }
//
//        if (logger.isDebugEnabled()) logger.debug("Parsing directory '{}' ...", dir.getAbsolutePath());
//        File[] files = dir.listFiles(file -> {
//            String name = file.getName();
//            if(name.startsWith(".")) {
//                // ignore hidden files & directories
//                return false;
//            }
//
//            if(file.isDirectory()) {
//                // accept any not hidden directory
//                return true;
//            }
//
//            // accept only .json files
//            return name.endsWith(".json");
//        });
//
//        if(files == null || files.length == 0){
//            if (logger.isDebugEnabled()) logger.debug("No such files found in directory '{}'.", dir.getAbsolutePath());
//            return null;
//        }
//
//        List<SiteNodeInfo> nodes = new ArrayList<>(files.length);
//
//        Map<String, File> filesMap = new HashMap<>();
//        for (File file : files) {
//            String name = normalizeFileName(file.getName());
//            filesMap.put(name, file);
//        }
//
//        boolean changed = false;
//
//        List<JsonInfo> children = jsonInfos.getChildren();
//        for (JsonInfo jsonInfo : children) {
//            String name = jsonInfo.getName();
//
//            // pre-sort
//            File file = filesMap.get(name);
//            if(file == null || !file.exists()){
//                if (logger.isInfoEnabled()) logger.info("No file '{}' found! Removing it from info index.", name);
//                continue;
//            }
//
//            // remove already existing file
//            filesMap.remove(name);
//
//            SiteNodeInfo nodeInfo;
//            if(file.isDirectory()){
//                JsonDirectoryInfo jsonDirectoryInfo = JsonDirectoryInfo.class.cast(jsonInfo);
//                nodeInfo = jsonDirectoryInfo.buildNodeInfo();
//                nodeInfo.setPath(localPath);
//
//                String dirPath;
//                if("/".equals(localPath)) {
//                    dirPath = localPath + name;
//                }
//                else {
//                    dirPath = localPath + '/' + name;
//                }
//
//                JsonInfoList dirJsonInfoList = readJsonInfoList(dirPath);
//                if(dirJsonInfoList == null){
//                    dirJsonInfoList = new JsonInfoList(dirPath);
//                    changed = true;
//                }
//
//                List<SiteNodeInfo> dirNodes = parseDirectory(file, dirPath, (SiteNodeContainerInfo) nodeInfo, dirJsonInfoList);
//                ((DirectoryInfo) nodeInfo).setNodes(dirNodes);
//            }
//            else {
//                JsonFileInfo jsonFileInfo = JsonFileInfo.class.cast(jsonInfo);
//                nodeInfo = jsonFileInfo.buildNodeInfo();
//                nodeInfo.setPath(localPath);
//            }
//
//            nodeInfo.setParent(parent);
//
//            nodes.add(nodeInfo);
//        }
//
//        for (File file : filesMap.values()) {
//            String name = normalizeFileName(file.getName());
//
//            if (logger.isDebugEnabled()) logger.debug("Processing '{}'.", name);
//
//            SiteNodeInfo nodeInfo;
//
//            if(file.isDirectory()){
//                if (logger.isDebugEnabled()) logger.debug(" - processing as sub-directory.");
//
//                if (logger.isDebugEnabled()) logger.debug(" - add a new directory info for {}", name);
//
//                nodeInfo = createNewDirectoryInfo(name, localPath);
//
//                JsonInfo jsonInfo = new JsonDirectoryInfo((DirectoryInfo) nodeInfo);
//
//                children.add(jsonInfo);
//                changed = true;
//
//                String dirPath;
//                if("/".equals(localPath)) {
//                    dirPath = localPath + name;
//                }
//                else {
//                    dirPath = localPath + '/' + name;
//                }
//                JsonInfoList dirJsonInfoList = readJsonInfoList(dirPath);
//                if(dirJsonInfoList == null){
//                    dirJsonInfoList = new JsonInfoList(dirPath);
//                    changed = true;
//                }
//
//                List<SiteNodeInfo> dirNodes = parseDirectory(file, dirPath, (SiteNodeContainerInfo) nodeInfo, dirJsonInfoList);
//                ((DirectoryInfo) nodeInfo).setNodes(dirNodes);
//            }
//            else {
//                if (logger.isDebugEnabled()) logger.debug(" - processing as file.");
//
//                if (logger.isDebugEnabled()) logger.debug(" - add a new page info for {}", name);
//
//                nodeInfo = createNewPageInfo(name, localPath);
//
//                JsonInfo jsonInfo  = new JsonFileInfo((PageInfo) nodeInfo);
//
//                children.add(jsonInfo);
//                changed = true;
//            }
//
//            nodeInfo.setParent(parent);
//
//            nodes.add(nodeInfo);
//        }
//
//        if(changed) {
//            writeJsonInfoList(localPath, jsonInfos);
//        }
//
//        return nodes;
//    }
//
//    private SiteNodeInfo selectInfo(SiteNodeContainerInfo base, String path){
//        if(base == null){
//            throw new IllegalArgumentException("Unable to find info based on <null>!");
//        }
//
//        // clean path
//        path = path.replace("//", "/").trim();
//
//        if (logger.isDebugEnabled()) logger.debug("Selecting info for base {} and path '{}'.", base.getPath(), path);
//
//        SiteNodeInfo current = (SiteNodeInfo) base;
//        StringTokenizer tok = new StringTokenizer(path, "/");
//        while (tok.hasMoreTokens()) {
//            String token = tok.nextToken().trim();
//
//            if (logger.isDebugEnabled()) logger.debug(" - processing token '{}'.", token);
//
//            if(!(SiteNodeContainerInfo.class.isAssignableFrom(current.getClass()))){
//                throw new MissingSiteDirectoryException(token, current);
//            }
//
//            List<SiteNodeInfo> children = ((SiteNodeContainerInfo) current).getNodes();
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
//                if (logger.isDebugEnabled()) logger.debug("Unable to find a node for name '{}' in {}.", token, children);
//                throw new UnresolvableSiteStructurePathException(token, path);
//            }
//
//            current = found;
//        }
//
//        return current;
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
//        String filename = directoryInfoFileName;
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
//        File directory;
//        if("/".equals(parentPath)){
//            directory = rootDirectory;
//        }
//        else {
//            directory = new File(rootDirectory, parentPath);
//        }
//
//        File infoFile = new File(directory, filename);
//
//        if (logger.isTraceEnabled()) logger.trace("Resolved info file '{}'", infoFile.getAbsolutePath());
//
//        return infoFile;
//    }
//
//    private JsonInfoList readJsonInfoList(String path) {
//        File infoFile = getInfoFile(path);
//
//        try (InputStream is = new BufferedInputStream(new FileInputStream(infoFile))) {
//            return objectMapper.readValue(is, JsonInfoList.class);
//        } catch (IOException ex) {
//            if (logger.isErrorEnabled()) logger.error("Error reading info file ({})!", infoFile.getAbsolutePath(), ex);
//            throw new IllegalStateException("Unable to read info file (" + infoFile.getAbsolutePath() + ")", ex);
//        }
//    }
//
//    private void writeJsonInfoList(String path, JsonInfoList infoList) {
//        File infoFile = getInfoFile(path);
//
//        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))) {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, infoList);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing info file (" + infoFile.getAbsolutePath() + ")!", ex);
//        }
//    }
//
//    private Map<String, String> readSiteStructureAttributes() {
//        File structureFile = getSiteDescriptionFile();
//
//        SiteStructureAttributes result;
//
//        try(InputStream is = new BufferedInputStream(new FileInputStream(structureFile))) {
//            result = objectMapper.readValue(is, SiteStructureAttributes.class);
//        } catch (IOException ex) {
//            if (logger.isErrorEnabled()) logger.error("Error reading site-structure attributes ({})!", structureFile.getAbsolutePath(), ex);
//            result = null;
//        }
//
//        if(result == null){
//            result = new SiteStructureAttributes();
//
//            if (logger.isDebugEnabled()) logger.debug("Creating empty site attributes.");
//            writeSiteStructureAttributes(result);
//        }
//
//        return result;
//    }
//
//    private void writeSiteStructureAttributes(Map<String, String> structure) {
//        File structureFile = getSiteDescriptionFile();
//
//        try(OutputStream os = new BufferedOutputStream(new FileOutputStream(structureFile))) {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, structure);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing site-structure attributes (" + structureFile.getAbsolutePath() + ")!", ex);
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
//        String filename = siteStructureFileName;
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
//    private ContentPage readContentPage(PageInfo info, File contentFile){
//        InputStream is = null;
//        ContentPage result;
//        try
//        {
//            is = new BufferedInputStream(new FileInputStream(contentFile));
//            result = objectMapper.readValue(is, ContentPage.class);
//        }
//        catch(IOException ex)
//        {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error loading content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
//        }
//        finally
//        {
//            IOUtils.closeQuietly(is);
//        }
//
//        if(result == null)
//        {
//            if(logger.isWarnEnabled())
//                logger.warn("Error reading content file '{}'", contentFile.getAbsolutePath());
//
//            return null;
//        }
//
//        result.setId(info.getName()); // why?
//
//        return result;
//    }
//
//    private void writeContentPage(PageInfo info, ContentPage contentPage, File contentFile){
//        OutputStream os = null;
//        try
//        {
//            os = new BufferedOutputStream(new FileOutputStream(contentFile));
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, contentPage);
//        }
//        catch(IOException ex)
//        {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing content for id '" + info.getName() + "' (" + contentFile.getAbsolutePath() + ")!", ex);
//        }
//        finally
//        {
//            IOUtils.closeQuietly(os);
//        }
//    }
//
//    private File getContentFile(PageInfo info) {
//        File contentRootFile = new File(contentRoot);
//
//        //noinspection ResultOfMethodCallIgnored
//        contentRootFile.mkdirs();
//        if (!contentRootFile.isDirectory()) {
//            throw new IllegalArgumentException("contentRoot '" + contentRootFile.getAbsolutePath() + "' is not a directory!");
//        }
//
//        String filename = info.getName() + ".json";
//
//        // remove first '/'
//        if (filename.startsWith("/")) {
//            filename = filename.substring(1);
//        }
//
//        // replace all '/' with '_'
//        filename = filename.replace('/', '_');
//
//        File directory = new File(contentRootFile, info.getPath());
//
//        File contentFile = new File(directory, filename);
//
//        if(logger.isDebugEnabled()) logger.debug("Resolved content file '{}'", contentFile.getAbsolutePath());
//
//        return contentFile;
//    }
//
//    private ReadWriteLock getLock(String id) {
//        ReadWriteLock result = locks.get(id);
//        if(result != null){
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
//    public void setObjectMapper(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    public void setContentRoot(String contentRoot) {
//        this.contentRoot = contentRoot;
//    }
//
//    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
//        this.defaultTraversingStrategy = defaultTraversingStrategy;
//    }
//
//    public void setSiteStructureFileName(String siteStructureFileName) {
//        this.siteStructureFileName = siteStructureFileName;
//    }
//
//    public void setDirectoryInfoFileName(String directoryInfoFileName) {
//        this.directoryInfoFileName = directoryInfoFileName;
//    }
//}
