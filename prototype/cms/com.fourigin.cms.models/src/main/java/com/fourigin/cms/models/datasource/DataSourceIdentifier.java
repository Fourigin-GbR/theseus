package com.fourigin.cms.models.datasource;

import java.io.Serializable;
import java.util.Map;

public class DataSourceIdentifier implements Serializable {

    private static final long serialVersionUID = 4193612352125682106L;

    private String type;
    private Map<String, Object> query;
    private String checksum;

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

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceIdentifier)) return false;

        DataSourceIdentifier that = (DataSourceIdentifier) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        //noinspection SimplifiableIfStatement
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        return checksum != null ? checksum.equals(that.checksum) : that.checksum == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataSourceIdentifier{" +
          "type='" + type + '\'' +
          ", query=" + query +
          ", checksum='" + checksum + '\'' +
          '}';
    }
}
