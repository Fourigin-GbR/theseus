package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.datasource.DataSource;
import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.datasource.DataSourceQueryCreationException;
import com.fourigin.argo.models.datasource.DataSourceQueryFactory;
import com.fourigin.argo.models.datasource.DataSourceResolvingException;
import com.fourigin.argo.models.datasource.DataSourceResolvingExceptions;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.datasource.index.IndexAwareDataSource;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepository;
import org.apache.commons.lang3.SerializationUtils;
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

    public static final String CTX_BASE = "base";

    public static final String CTX_CUSTOMER = "customer";

    public ContentPage resolve(PageInfo ownerPage, ContentRepository contentRepository, ContentPage contentPage, String customer, String base) {
        if (!contentPage.hasDataSourceContents()) {
            if (logger.isDebugEnabled()) logger.debug("No dataSources defined, nothing to resolve.");
            return contentPage;
        }

        ContentPage result = SerializationUtils.clone(contentPage);
        Collection<DataSourceContent> dataSourceGroups = result.getDataSourceContents();

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
            if (dataSource == null) {
                if (logger.isErrorEnabled())
                    logger.error("No dataSource found for type '{}' (name: '{}')!", type, name);
                errors.add(new UnknownDataSourceTypeException(type));
                continue;
            }

            Map<String, Object> queryMap = dataSourceId.getQuery();

            DataSourceQuery query;
            try {
                query = DataSourceQueryFactory.buildFromMap(dataSource, queryMap);
            } catch (DataSourceQueryCreationException ex) {
                if (logger.isErrorEnabled())
                    logger.error("Unable to create query of type '{}' (name: '{}') from map {}!", type, name, queryMap);
                errors.add(ex);
                continue;
            }

            Map<String, Object> context = new HashMap<>();

            context.put(CTX_BASE, base);
            context.put(CTX_CUSTOMER, customer);

            if(ContentResolverAwareDataSource.class.isAssignableFrom(dataSource.getClass())){
                context.put(ContentResolverAwareDataSource.CTX_CONTENT_RESOLVER, contentRepository);
            }

            String previousChecksum = dataSourceId.getChecksum();
            List<ContentElement> resolvedContent = dataSource.generateContent(ownerPage, dataSourceId, query, context);
            String newChecksum = ChecksumGenerator.getChecksum(resolvedContent);
            if (newChecksum.equals(previousChecksum)) {
                if (logger.isInfoEnabled())
                    logger.info("Resolved unchanged data (name: '{}') with checksum '{}'.", name, newChecksum);
                continue;
            }

            if (logger.isInfoEnabled())
                logger.info("Resolved changed data (name: '{}') with checksum '{}'.", name, newChecksum);
            dataSourceId.setChecksum(newChecksum);
            dataSourceGroup.setContent(resolvedContent);

            if (IndexAwareDataSource.class.isAssignableFrom(dataSource.getClass())) {
                if (logger.isDebugEnabled()) logger.debug("Generating index.");
                // TODO: is there a better way without cast?
                IndexAwareDataSource<DataSourceQuery> indexAwareDataSource = (IndexAwareDataSource<DataSourceQuery>) dataSource;
                DataSourceIndex index = indexAwareDataSource.generateIndex(ownerPage, dataSourceId, query, resolvedContent);
                if (index != null) {
                    if (logger.isDebugEnabled()) logger.debug("Writing the index {}.", index.getName());
                    contentRepository.createIndex(ownerPage, index);
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new DataSourceResolvingExceptions(errors);
        }

        return result;
    }

    public void setDataSources(Collection<DataSource<? extends DataSourceQuery>> dataSources) {
        this.dataSources = dataSources;
    }
}
