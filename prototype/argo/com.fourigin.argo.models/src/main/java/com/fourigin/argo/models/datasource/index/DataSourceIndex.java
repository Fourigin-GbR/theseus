package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataSourceIndex implements Serializable {
    private static final long serialVersionUID = 6264288619972132472L;

    private String name;

    /*
        "references": [
            "object_001",
            "object_002",
            "object_003"
        ]
     */
    private List<String> references;

    /*
    	"targets": [
            "/objects/rent/object_001",
            "/objects/rent/object_002",
            "/objects/rent/object_003",
        ]
     */
    private List<String> targets;

    /*
        "categories": {
            "region": {
                "loutraki": [0, 1],
                "athen": [2]
            },
            ...
        }
     */
    private Map<String, Map<String, List<Integer>>> categories;

    /*
        "fields": [
            {
                "name": "code",
                "type": "TEXT",
                "value": [
                    "001",
                    "002",
                    "003"
                ]
            },
            {
                "name": "number-of-rooms",
                "type": "NUMBER",
                "value": [
                    "3",
                    "2",
                    "3"
                ]
            },
            {
                "name": "max-number-of-guests",
                "type": "NUMBER",
                "value": [
                    "3",
                    "3",
                    "6"
                ]
            },
            {
                "name": "price",
                "type": "PRICE",
                "value": [
                    "490",
                    "520",
                    "500"
                ]
            },
            {
                "name": "distance",
                "type": "NUMBER",
                "value": [
                    "200",
                    "250",
                    "0"
                ]
            },
            {
                "name": "location-properties",
                "type": "LIST_OF_TEXT",
                "value": [
                    ["ruhige Lage", "Supermarkt in der Nähe"],
                    [],
                    ["direkt am Strand", "an der Promenade"]
                ]
            }
        ]
     */
    private List<FieldValue> fields;

    /*
        "searchValues": {
            "promenade": [2],
            "supermarkt": [0, 1],
            "ruhig": [0],
            "villa": [0, 2],
            "schwimmbad": [0, 1],
            "spa": [0, 1],
            "loutraki": [0, 1],
            "athen": [2]
        }
     */
    private Map<String, Set<Integer>> searchValues;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public Map<String, Map<String, List<Integer>>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Map<String, List<Integer>>> categories) {
        this.categories = categories;
    }

    public List<FieldValue> getFields() {
        return fields;
    }

    public void setFields(List<FieldValue> fields) {
        this.fields = fields;
    }

    public Map<String, Set<Integer>> getSearchValues() {
        return searchValues;
    }

    public void setSearchValues(Map<String, Set<Integer>> searchValues) {
        this.searchValues = searchValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceIndex)) return false;
        DataSourceIndex that = (DataSourceIndex) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(references, that.references) &&
            Objects.equals(targets, that.targets) &&
            Objects.equals(categories, that.categories) &&
            Objects.equals(fields, that.fields) &&
            Objects.equals(searchValues, that.searchValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, references, targets, categories, fields, searchValues);
    }

    @Override
    public String toString() {
        return "DataSourceIndex{" +
            "name='" + name + '\'' +
            ", references=" + references +
            ", targets=" + targets +
            ", categories=" + categories +
            ", fields=" + fields +
            ", searchValues=" + searchValues +
            '}';
    }
}
