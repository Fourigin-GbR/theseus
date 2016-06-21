package com.fourigin.cms.models.datasource;

import java.io.Serializable;
import java.util.Map;

public class DataSourceIdentifier implements Serializable {

    private static final long serialVersionUID = 4193612352125682106L;

    private String type;
    private Map<String, Object> query;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "DataSourceIdentifier{" +
          "type='" + type + '\'' +
          ", query=" + query +
          '}';
    }
}
