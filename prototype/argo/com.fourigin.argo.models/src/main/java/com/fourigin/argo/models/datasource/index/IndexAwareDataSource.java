package com.fourigin.argo.models.datasource.index;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.structure.nodes.PageInfo;

public interface IndexAwareDataSource<T extends DataSourceQuery> {
    DataSourceIndex generateIndex(PageInfo info, DataSourceIdentifier id, T query, ContentElement generatedContent);
}
