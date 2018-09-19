package com.fourigin.argo.controller.search;

import java.io.Serializable;
import java.util.Objects;

public class FieldValueComparator implements Serializable {
    private static final long serialVersionUID = 5160780008972810941L;

    private String comparator;
    private String value;

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldValueComparator)) return false;
        FieldValueComparator that = (FieldValueComparator) o;
        return Objects.equals(comparator, that.comparator) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparator, value);
    }

    @Override
    public String toString() {
        return "FieldValueComparator{" +
            "comparator='" + comparator + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
