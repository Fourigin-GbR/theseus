package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.datasource.DataSourceQueryCreationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SiteStructureDataSourceQuery implements DataSourceQuery {

    private static final String INFO_PATH = "INFO_PATH";
    private static final String VERBOSE = "VERBOSE";
    private static final String IGNORE_OWNER_PAGE = "IGNORE_OWNER_PAGE";
    private static final String INCLUDE_CONTENT = "INCLUDE_CONTENT";

    private String path;

    private boolean verbose = true;

    private boolean ignoreOwnerPage = false;

    private List<String> contentReferences;

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

        Object contentToInclude = values.get(INCLUDE_CONTENT);
        if (contentToInclude != null) {
            contentReferences = new ArrayList<>();

            String[] parts = String.valueOf(contentToInclude).split(",");
            for (String part : parts) {
                contentReferences.add(part.trim());
            }
        }
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

    public List<String> getContentReferences() {
        return contentReferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteStructureDataSourceQuery)) return false;
        SiteStructureDataSourceQuery that = (SiteStructureDataSourceQuery) o;
        return verbose == that.verbose &&
            ignoreOwnerPage == that.ignoreOwnerPage &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentReferences, that.contentReferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, verbose, ignoreOwnerPage, contentReferences);
    }

    @Override
    public String toString() {
        return "SiteStructureDataSourceQuery{" +
            "path='" + path + '\'' +
            ", verbose=" + verbose +
            ", ignoreOwnerPage=" + ignoreOwnerPage +
            ", contentReferences=" + contentReferences +
            '}';
    }
}
