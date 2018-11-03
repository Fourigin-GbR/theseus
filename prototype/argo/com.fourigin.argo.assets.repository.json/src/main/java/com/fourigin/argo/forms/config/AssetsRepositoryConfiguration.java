package com.fourigin.argo.forms.config;

import com.fourigin.argo.assets.repository.BlobBasedAssetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class AssetsRepositoryConfiguration {

    @Bean
    public BlobBasedAssetRepository blobBasedAssetRepository(
        @Value("${blob-asset-repository.root-path}") String basePath
    ) {
        File baseFile = new File(basePath);
        return new BlobBasedAssetRepository(baseFile);
    }
}
