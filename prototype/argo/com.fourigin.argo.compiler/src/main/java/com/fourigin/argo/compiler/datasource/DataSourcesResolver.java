package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataSourcesResolver {
    private Collection<DataSource> dataSources;

    private final Logger logger = LoggerFactory.getLogger(DataSourcesResolver.class);

    public ContentPage resolve(ContentPage contentPage){
        Collection<DataSourceContent> dataSourceGroups = contentPage.getDataSourceContents();
        if(dataSourceGroups == null || dataSourceGroups.isEmpty()){
            if (logger.isDebugEnabled()) logger.debug("No dataSources defined, nothing to resolve.");
            return contentPage;
        }

        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            dataSourceMap.put(dataSource.getType(), dataSource);
        }

        for (DataSourceContent dataSourceGroup : dataSourceGroups) {
            DataSourceIdentifier dataSourceId = dataSourceGroup.getIdentifier();
            String type = dataSourceId.getType();
            DataSource dataSource = dataSourceMap.get(type);
            if(dataSource == null){
                if (logger.isErrorEnabled()) logger.error("No dataSource found for type '{}'!", type);
                continue;
            }

            Map<String, Object> queryMap = dataSourceId.getQuery();
            DataSourceQuery query = DataSourceQueryFactory.buildFromMap(dataSource, queryMap);
            if(query == null){
                if (logger.isErrorEnabled()) logger.error("Unable to create query of type '{}' from map {}!", type, queryMap);
                continue;
            }

            String previousChecksum = dataSourceId.getChecksum();
            DataSourceResponse response = dataSource.apply(query);
            String newChecksum = response.getChecksum();
            if(newChecksum.equals(previousChecksum)){
                if (logger.isInfoEnabled()) logger.info("Resolved unchanged data with checksum '{}'.", newChecksum);
                continue;
            }

            if (logger.isInfoEnabled()) logger.info("Resolved changed data with checksum '{}'.", newChecksum);
            ContentElement resolvedContent = response.getContent();
            dataSourceGroup.setContent(resolvedContent);
        }

        return contentPage;
    }

    public void setDataSources(Collection<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
