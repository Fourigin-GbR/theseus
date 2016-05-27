package com.fourigin.hera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(HeraProperties.class)
public class HeraAutoConfiguration {

    private HeraProperties heraProperties;

    private final Logger logger = LoggerFactory.getLogger(HeraAutoConfiguration.class);

    @PostConstruct
    public void logConfiguration(){
        if (logger.isInfoEnabled()) logger.info("Hera configuration properties:\n{}", heraProperties);
    }

    @Autowired
    public void setHeraProperties(HeraProperties heraProperties) {
        this.heraProperties = heraProperties;
    }
}
