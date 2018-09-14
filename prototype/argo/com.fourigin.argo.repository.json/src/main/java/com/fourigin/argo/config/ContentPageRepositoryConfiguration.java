package com.fourigin.argo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule;
import com.fourigin.argo.repository.DirectoryContentBasedTemplateResolver;
import com.fourigin.argo.repository.FileBasedRuntimeConfigurationResolverFactory;
import com.fourigin.argo.repository.HiddenDirectoryContentRepositoryFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.repository.model.mapping.JsonInfoModule;
import com.fourigin.argo.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentPageRepositoryConfiguration {

    @Bean
    public HiddenDirectoryContentRepositoryFactory jsonFilesContentRepositoryFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Value("${content-page-repository.key-name}") String keyName,
        @Autowired PageInfoTraversingStrategy defaultTraversingStrategy
    ) {
        HiddenDirectoryContentRepositoryFactory factory = new HiddenDirectoryContentRepositoryFactory();

        factory.setBasePath(basePath);
        factory.setKeyName(keyName);
        factory.setDefaultTraversingStrategy(defaultTraversingStrategy);
        factory.setObjectMapper(objectMapper());

        return factory;
    }

    @Bean
    public PageInfoTraversingStrategy traversingStrategy(){
        return new DefaultPageInfoTraversingStrategy();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new ContentPageModule());
        objectMapper.registerModule(new JsonInfoModule());

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        return objectMapper;
    }

    @Bean
    public ContentPageModule contentPageModule(){
        return new ContentPageModule();
    }

    @Bean
    public JsonInfoModule jsonInfoModule(){
        return new JsonInfoModule();
    }

    @Bean
    public TemplateResolver templateResolver(
        @Value("${template.engine.thymeleaf.base}") String templateBasePath
    ) {
        return new DirectoryContentBasedTemplateResolver(templateBasePath, objectMapper());
//        return id -> {
//            File baseDir = new File(templateBasePath);
//            String templateReference = id;
//            int pos = templateReference.indexOf('.');
//            while(pos >= 0){
//                String subDir = templateReference.substring(0, pos);
//                templateReference = templateReference.substring(pos+1);
//                baseDir = new File(baseDir, subDir);
//            }
//
//            final String templateBaseName = templateReference;
//            File[] matchingFiles = baseDir.listFiles((dir, name) -> name.startsWith(templateBaseName) && name.endsWith(".html"));
//
//            Map<String, String> checksums = new HashMap<>();
//
//            // read all available variation files
//            Set<TemplateVariation> variations = new HashSet<>();
//            if(matchingFiles != null && matchingFiles.length > 0){
//                for (File matchingFile : matchingFiles) {
//                    String fileName = matchingFile.getName();
//                    String variationName = "default";
//                    int dot = fileName.indexOf(".html");
//                    int separator = fileName.indexOf('#');
//                    if(separator >= 0){
//                        variationName = fileName.substring(separator+1, dot);
//                    }
//
//                    TemplateVariation variation = new TemplateVariation();
//                    variation.setId(variationName);
//                    variation.setType(Type.THYMELEAF);
//                    variation.setOutputContentType("text/html");
//                    variations.add(variation);
//
//                    checksums.put(fileName, ChecksumGenerator.getChecksum(variation));
//                }
//            }
//
//            // build the template's revision
//            List<String> keys = new ArrayList<>(checksums.keySet());
//            Collections.sort(keys);
//            StringBuilder revision = new StringBuilder();
//            for (String key : keys) {
//                if(revision.length() > 0){
//                    revision.append('-');
//                }
//                revision.append(key);
//            }
//
//            Template template = new Template();
//            template.setId(id);
//            template.setRevision(revision.toString());
//            template.setVariations(variations);
//
//            // read prototype, if available
//            File prototypeFile = new File(baseDir, id + ".json");
//            if(prototypeFile.exists()){
//                try(InputStream is = new BufferedInputStream(new FileInputStream(prototypeFile))) {
//                    ContentPagePrototype prototype = objectMapper.readValue(is, ContentPagePrototype.class);
//                    template.setPrototype(prototype);
//                }
//                catch(IOException ex)
//                {
//                    // TODO: create proper exception handling
//                    throw new IllegalArgumentException("Error loading content prototype for id '" + id + "' (" + prototypeFile.getAbsolutePath() + ")!", ex);
//                }
//            }
//
//            return template;
//        };
    }

    @Bean
    public FileBasedRuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Value("${content-page-repository.key-name}") String keyName
    ){
        FileBasedRuntimeConfigurationResolverFactory factory = new FileBasedRuntimeConfigurationResolverFactory();

        factory.setBasePath(basePath);
        factory.setKeyName(keyName);

        return factory;
    }
}
