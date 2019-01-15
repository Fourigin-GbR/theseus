package com.fourigin.argo.controller.search;

import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.datasource.index.DataSourceIndexProcessing;
import com.fourigin.argo.repository.DataSourceIndexResolver;
import com.fourigin.argo.repository.DataSourceIndexResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/{customer}/search")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private DataSourceIndexResolverFactory dataSourceIndexResolverFactory;

    public SearchController(DataSourceIndexResolverFactory dataSourceIndexResolverFactory) {
        this.dataSourceIndexResolverFactory = dataSourceIndexResolverFactory;
    }

    @RequestMapping("/")
    @ResponseBody
    public List<String> resolveMatchingIndexTargets(
        @PathVariable("customer") String customer,
        @RequestParam("base") String base,
        @RequestParam("path") String path,
        @RequestBody SearchRequest request
    ) {
        if (logger.isDebugEnabled()) logger.debug("Resolving matching index for {}/{} and {}", base, path, request);

        MDC.put("base", base);

        try {
            DataSourceIndexResolver resolver = dataSourceIndexResolverFactory.getInstance(customer, base);
            if (resolver == null) {
                throw new IllegalArgumentException("No data-source index resolver found for customer '" + customer + "' and base '" + base + "'!");
            }

            String indexName = request.getIndex();
            DataSourceIndex index = resolver.resolveIndex(base, path, indexName);
            if (index == null) {
                throw new IllegalArgumentException("No index found for base '" + base + "', path '" + path + "' and name '" + indexName + "'!");
            }

            if (logger.isDebugEnabled()) logger.debug("Resolved index: {}", index);

            List<String> references = DataSourceIndexProcessing.resolveMatchingIndexTargets(index, request.getCategories(), request.getFields());
            if (logger.isDebugEnabled()) logger.debug("References: {}", references);

            return references;
        } finally {
            MDC.remove("base");
        }
    }
}
