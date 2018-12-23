package com.fourigin.argo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.assets.repository.BlobBasedAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class AssetsRepositoryConfiguration {

    @Bean
    public BlobBasedAssetRepository blobBasedAssetRepository(
        @Value("${blob-asset-repository.root-path}") String basePath,
        @Autowired ObjectMapper objectMapper
    ) {
        File baseFile = new File(basePath);

        BlobBasedAssetRepository result = new BlobBasedAssetRepository(baseFile);
        result.setObjectMapper(objectMapper);
        return result;
    }
}
