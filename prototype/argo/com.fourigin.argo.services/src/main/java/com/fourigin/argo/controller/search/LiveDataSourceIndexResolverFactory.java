package com.fourigin.argo.controller.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.config.CustomerSpecificConfiguration;
import com.fourigin.argo.repository.DataSourceIndexResolver;
import com.fourigin.argo.repository.DataSourceIndexResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LiveDataSourceIndexResolverFactory implements DataSourceIndexResolverFactory {

    private ObjectMapper objectMapper;

    private CustomerSpecificConfiguration customerSpecificConfiguration;

    private final Logger logger = LoggerFactory.getLogger(LiveDataSourceIndexResolverFactory.class);

    @Override
    public DataSourceIndexResolver getInstance(String customer, String base) {
        Map<String, Map<String, String>> docRoots = customerSpecificConfiguration.getDocumentRoots();
        Map<String, String> customerDocRoots = docRoots.get(customer);
        String path = customerDocRoots.get(base);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new LiveDataSourceIndexResolver instance for base '{}' and path '{}'.", base, path);
        return new LiveDataSourceIndexResolver(path, objectMapper);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setCustomerSpecificConfiguration(CustomerSpecificConfiguration customerSpecificConfiguration) {
        this.customerSpecificConfiguration = customerSpecificConfiguration;
    }
}
