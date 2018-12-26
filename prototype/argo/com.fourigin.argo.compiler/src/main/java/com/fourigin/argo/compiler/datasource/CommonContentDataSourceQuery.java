package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.datasource.DataSourceQuery;
import com.fourigin.argo.models.datasource.DataSourceQueryCreationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommonContentDataSourceQuery implements DataSourceQuery {

    private static final String VERBOSE = "VERBOSE";
    private static final String INCLUDE_CONTENT = "INCLUDE_CONTENT";

    private boolean verbose = true;

    private List<String> contentReferences;

    @Override
    public void buildFromMap(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            throw new DataSourceQueryCreationException("Initialization parameter map is empty!", values);
        }

        this.verbose = Boolean.parseBoolean(String.valueOf(values.get(VERBOSE)));

        Object contentToInclude = values.get(INCLUDE_CONTENT);
        if (contentToInclude != null) {
            contentReferences = new ArrayList<>();

            String[] parts = String.valueOf(contentToInclude).split(",");
            for (String part : parts) {
                contentReferences.add(part.trim());
            }
        }
    }

    public boolean isVerbose() {
        return verbose;
    }

    public List<String> getContentReferences() {
        return contentReferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonContentDataSourceQuery)) return false;
        CommonContentDataSourceQuery that = (CommonContentDataSourceQuery) o;
        return verbose == that.verbose &&
            Objects.equals(contentReferences, that.contentReferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verbose, contentReferences);
    }

    @Override
    public String toString() {
        return "CommonContentDataSourceQuery{" +
            "verbose=" + verbose +
            ", contentReferences=" + contentReferences +
            '}';
    }
}
