package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.repository.ContentResolver;

public interface DataSource<T extends DataSourceQuery> {
    String getType();
    ContentElement generateContent(ContentResolver contentResolver, DataSourceIdentifier id, T query);
}
