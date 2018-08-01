package com.fourigin.argo.compiler;

import com.fourigin.argo.compiler.datasource.DataSourceQueryBuilder;
import com.fourigin.argo.compiler.datasource.DataSourceQueryFactory;
import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.datasource.EmptyDataSourceQuery;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSource;
import com.fourigin.argo.compiler.datasource.SiteStructureDataSourceQuery;
import com.fourigin.argo.compiler.datasource.TimestampDataSource;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.strategies.DefaultFilenameStrategy;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.template.engine.TemplateEngineFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultPageCompilerFactory implements PageCompilerFactory {
    private ContentRepositoryFactory contentRepositoryFactory;
    private TemplateEngineFactory templateEngineFactory;
    private TemplateResolver templateResolver;
    private DataSourcesResolver dataSourcesResolver;
    private String preparedContentRoot;

    public DefaultPageCompilerFactory(
        ContentRepositoryFactory contentRepositoryFactory,
        TemplateEngineFactory templateEngineFactory,
        TemplateResolver templateResolver,
        DataSourcesResolver dataSourcesResolver,
        String preparedContentRoot
    ) {
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.templateEngineFactory = templateEngineFactory;
        this.templateResolver = templateResolver;
        this.dataSourcesResolver = dataSourcesResolver;
        this.preparedContentRoot = preparedContentRoot;
    }

    @Override
    public PageCompiler getInstance(String base) {
        Map<String, DataSourceQueryBuilder> queryBuilders = new HashMap<>();
        queryBuilders.put(TimestampDataSource.TYPE, new DataSourceQueryBuilder(EmptyDataSourceQuery.class));
        queryBuilders.put(SiteStructureDataSource.TYPE, new DataSourceQueryBuilder(SiteStructureDataSourceQuery.class));
        DataSourceQueryFactory.setBuilders(queryBuilders);

        DefaultPageCompiler compiler = new DefaultPageCompiler(base);

        compiler.setContentRepository(contentRepositoryFactory.getInstance(base));
        compiler.setTemplateEngineFactory(templateEngineFactory);
        compiler.setTemplateResolver(templateResolver);
        compiler.setDataSourcesResolver(dataSourcesResolver);

        FilenameStrategy preparedContentFilenameStrategy = new DefaultFilenameStrategy(false);

        DefaultCompilerInterceptor compilerInterceptor = new DefaultCompilerInterceptor(
            preparedContentRoot,
            base,
            preparedContentFilenameStrategy
        );
        compiler.setCompilerInterceptor(compilerInterceptor);

        return compiler;
    }
}
