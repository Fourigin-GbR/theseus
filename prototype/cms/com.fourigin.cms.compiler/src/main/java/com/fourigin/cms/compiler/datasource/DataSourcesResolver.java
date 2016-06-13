package com.fourigin.cms.compiler.datasource;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.DataSourceContent;
import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.content.elements.ContentGroup;
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
            String type = dataSourceGroup.getType();
            DataSource dataSource = dataSourceMap.get(type);
            if(dataSource == null){
                if (logger.isErrorEnabled()) logger.error("No dataSource found for type '{}'!", type);
                continue;
            }

            Map<String, Object> queryMap = dataSourceGroup.getQuery();
            DataSourceQuery query = DataSourceQueryFactory.buildFromMap(dataSource, queryMap);
            if(query == null){
                if (logger.isErrorEnabled()) logger.error("Unable to create query of type '{}' from map {}!", type, queryMap);
                continue;
            }

            String previousChecksum = dataSourceGroup.getChecksum();
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
