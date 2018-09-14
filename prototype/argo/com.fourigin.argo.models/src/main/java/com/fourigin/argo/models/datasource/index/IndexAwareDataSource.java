package com.fourigin.argo.models.datasource.index;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.structure.nodes.PageInfo;

import java.util.List;

public interface IndexAwareDataSource<T extends DataSourceQuery> {
    DataSourceIndex generateIndex(PageInfo info, DataSourceIdentifier id, T query, List<ContentElement> generatedContent);
}
