package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.datasource.DataSourceQueryCreationException;

import java.util.Map;

public class SiteStructureDataSourceQuery implements DataSourceQuery {

    private static final String INFO_PATH = "INFO_PATH";
    private static final String VERBOSE = "VERBOSE";
    private static final String IGNORE_OWNER_PAGE = "IGNORE_OWNER_PAGE";

    private String path;

    private boolean verbose = true;

    private boolean ignoreOwnerPage = false;

    @Override
    public void buildFromMap(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            throw new DataSourceQueryCreationException("Initialization parameter map is empty!", values);
        }

        Object pathValue = values.get(INFO_PATH);
        if (pathValue == null) {
            throw new DataSourceQueryCreationException("Missing mandatory initialization parameter '" + INFO_PATH + "'!", values);
        }

        this.path = String.valueOf(pathValue);

        this.verbose = Boolean.parseBoolean(String.valueOf(values.get(VERBOSE)));

        this.ignoreOwnerPage = Boolean.parseBoolean(String.valueOf(values.get(IGNORE_OWNER_PAGE)));
    }

    public String getPath() {
        return path;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isIgnoreOwnerPage() {
        return ignoreOwnerPage;
    }
}
