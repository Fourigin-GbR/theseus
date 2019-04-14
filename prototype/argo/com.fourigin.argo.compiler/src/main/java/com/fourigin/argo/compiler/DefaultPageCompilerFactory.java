package com.fourigin.argo.compiler;

import com.fourigin.argo.compiler.datasource.CommonContentDataSource;
import com.fourigin.argo.compiler.datasource.CommonContentDataSourceQuery;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.EmptyDataSourceQuery;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSourceQuery;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.compiler.processor.ContentPageProcessor;
import com.fourigin.argo.models.datasource.DataSourceQueryBuilder;
import com.fourigin.argo.models.datasource.DataSourceQueryFactory;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.RuntimeConfigurationResolverFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.strategies.DefaultFilenameStrategy;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.template.engine.ArgoTemplateEngineFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPageCompilerFactory implements PageCompilerFactory {
    private ContentRepositoryFactory contentRepositoryFactory;
    private ArgoTemplateEngineFactory templateEngineFactory;
    private TemplateResolver templateResolver;
    private DataSourcesResolver dataSourcesResolver;
    private String preparedContentRoot;
    private RuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory;
    private List<ContentPageProcessor> contentPageProcessors;
    private ProjectSpecificPathResolver pathResolver;

    public DefaultPageCompilerFactory(
        ContentRepositoryFactory contentRepositoryFactory,
        ArgoTemplateEngineFactory templateEngineFactory,
        TemplateResolver templateResolver,
        DataSourcesResolver dataSourcesResolver,
        RuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory,
        String preparedContentRoot,
        List<ContentPageProcessor> contentPageProcessors,
        ProjectSpecificPathResolver pathResolver
    ) {
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.templateEngineFactory = templateEngineFactory;
        this.templateResolver = templateResolver;
        this.dataSourcesResolver = dataSourcesResolver;
        this.runtimeConfigurationResolverFactory = runtimeConfigurationResolverFactory;
        this.preparedContentRoot = preparedContentRoot;
        this.contentPageProcessors = contentPageProcessors;
        this.pathResolver = pathResolver;
    }

    @Override
    public PageCompiler getInstance(String projectId, String language) {
        Map<String, DataSourceQueryBuilder> queryBuilders = new HashMap<>();
        queryBuilders.put(TimestampDataSource.TYPE, new DataSourceQueryBuilder(EmptyDataSourceQuery.class));
        queryBuilders.put(SiteStructureDataSource.TYPE, new DataSourceQueryBuilder(SiteStructureDataSourceQuery.class));
        queryBuilders.put(CommonContentDataSource.TYPE, new DataSourceQueryBuilder(CommonContentDataSourceQuery.class));
        DataSourceQueryFactory.setBuilders(queryBuilders);

        DefaultPageCompiler compiler = new DefaultPageCompiler(projectId, language);

        compiler.setContentRepository(contentRepositoryFactory.getInstance(projectId, language));
        compiler.setArgoTemplateEngineFactory(templateEngineFactory);
        compiler.setTemplateResolver(templateResolver);
        compiler.setDataSourcesResolver(dataSourcesResolver);

        FilenameStrategy filenameStrategy = new DefaultFilenameStrategy(false);

        if (contentPageProcessors != null) {
            compiler.setContentPageProcessors(contentPageProcessors);
        }

        String resolvePath = pathResolver.resolvePath(preparedContentRoot, projectId, language);

        DefaultCompilerInterceptor compilerInterceptor = new DefaultCompilerInterceptor(
            resolvePath,
            filenameStrategy
        );

        compiler.setCompilerInterceptors(Collections.singletonList(
            compilerInterceptor
        ));

        compiler.setRuntimeConfigurationResolver(runtimeConfigurationResolverFactory.getInstance(projectId, language));

        return compiler;
    }
}
