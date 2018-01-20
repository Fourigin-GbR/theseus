package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.repository.ContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSourcesResolver {
    private Collection<DataSource<? extends DataSourceQuery>> dataSources;

    private final Logger logger = LoggerFactory.getLogger(DataSourcesResolver.class);

    public ContentPage resolve(ContentResolver contentResolver, ContentPage contentPage){
        Collection<DataSourceContent> dataSourceGroups = contentPage.getDataSourceContents();
        if(dataSourceGroups == null || dataSourceGroups.isEmpty()){
            if (logger.isDebugEnabled()) logger.debug("No dataSources defined, nothing to resolve.");
            return contentPage;
        }

        Map<String, DataSource<DataSourceQuery>> dataSourceMap = new HashMap<>();
        for (DataSource<? extends DataSourceQuery> dataSource : dataSources) {
            // TODO: is there a better way without cast?
            dataSourceMap.put(dataSource.getType(), (DataSource<DataSourceQuery>) dataSource);
        }

        List<DataSourceResolvingException> errors = new ArrayList<>();

        for (DataSourceContent dataSourceGroup : dataSourceGroups) {
            DataSourceIdentifier dataSourceId = dataSourceGroup.getIdentifier();
            String name = dataSourceGroup.getName();
            String type = dataSourceId.getType();
            DataSource<DataSourceQuery> dataSource = dataSourceMap.get(type);
            if(dataSource == null){
                if (logger.isErrorEnabled()) logger.error("No dataSource found for type '{}' (name: '{}')!", type, name);
                errors.add(new UnknownDataSourceTypeException(type));
                continue;
            }

            Map<String, Object> queryMap = dataSourceId.getQuery();

            DataSourceQuery query;
            try {
                query = DataSourceQueryFactory.buildFromMap(dataSource, queryMap);
            }
            catch(DataSourceQueryCreationException ex){
                if (logger.isErrorEnabled()) logger.error("Unable to create query of type '{}' (name: '{}') from map {}!", type, name, queryMap);
                errors.add(ex);
                continue;
            }

            String previousChecksum = dataSourceId.getChecksum();
            ContentElement resolvedContent = dataSource.generateContent(contentResolver, query);
            String newChecksum = ChecksumGenerator.getChecksum(resolvedContent);
            if(newChecksum.equals(previousChecksum)){
                if (logger.isInfoEnabled()) logger.info("Resolved unchanged data (name: '{}') with checksum '{}'.", name, newChecksum);
                continue;
            }

            if (logger.isInfoEnabled()) logger.info("Resolved changed data (name: '{}') with checksum '{}'.", name, newChecksum);
            dataSourceId.setChecksum(newChecksum);
            dataSourceGroup.setContent(resolvedContent);
        }

        if(!errors.isEmpty()){
            throw new DataSourceResolvingExceptions(errors);
        }

        return contentPage;
    }

    public void setDataSources(Collection<DataSource<? extends DataSourceQuery>> dataSources) {
        this.dataSources = dataSources;
    }
}
