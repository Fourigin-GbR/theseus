package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.repository.ContentResolver;

public interface DataSource<DataSourceQuery> {
    String getType();
    ContentElement generateContent(ContentResolver contentResolver, DataSourceQuery query);
}
