package com.fourigin.argo.models.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ContentPageChecksum {
    private String metaDataValue;
    private String contentValue;
    private Map<String, String> dataSourceValues;

    public ContentPageChecksum() {
    }

    public ContentPageChecksum(String metaDataValue, String contentValue, Map<String, String> dataSourceValues) {
        this.metaDataValue = metaDataValue;
        this.contentValue = contentValue;
        this.dataSourceValues = dataSourceValues;
    }

    public String getMetaDataValue() {
        return metaDataValue;
    }

    public void setMetaDataValue(String metaDataValue) {
        this.metaDataValue = metaDataValue;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }

    public Map<String, String> getDataSourceValues() {
        return dataSourceValues;
    }

    public void setDataSourceValues(Map<String, String> dataSourceValues) {
        this.dataSourceValues = dataSourceValues;
    }

    public String getCombinedValue() {
        StringBuilder result = new StringBuilder(metaDataValue + '-' + contentValue);

        if (dataSourceValues != null) {
            Set<String> names = dataSourceValues.keySet();
            List<String> orderedNames = new ArrayList<>(names);
            Collections.sort(orderedNames);
            for (String name : orderedNames) {
                String checksum = dataSourceValues.get(name);
                result.append('-');
                result.append(checksum);
            }
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentPageChecksum)) return false;
        ContentPageChecksum that = (ContentPageChecksum) o;
        return Objects.equals(metaDataValue, that.metaDataValue) &&
            Objects.equals(contentValue, that.contentValue) &&
            Objects.equals(dataSourceValues, that.dataSourceValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaDataValue, contentValue, dataSourceValues);
    }

    @Override
    public String toString() {
        return getCombinedValue();
    }
}
