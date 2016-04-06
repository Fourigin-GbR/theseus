package com.fourigin.theseus.filters;

public class ModelObjectFilterEntity {
    private String field;
    private String filterValue;
    private FilterEntityRelation relation;

    public ModelObjectFilterEntity(String field, String filterValue, FilterEntityRelation relation) {
        this.field = field;
        this.filterValue = filterValue;
        this.relation = relation;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public FilterEntityRelation getRelation() {
        return relation;
    }

    public void setRelation(FilterEntityRelation relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelObjectFilterEntity)) return false;

        ModelObjectFilterEntity that = (ModelObjectFilterEntity) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        //noinspection SimplifiableIfStatement
        if (filterValue != null ? !filterValue.equals(that.filterValue) : that.filterValue != null) return false;
        return relation == that.relation;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (filterValue != null ? filterValue.hashCode() : 0);
        result = 31 * result + (relation != null ? relation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModelObjectFilterEntity{" +
          "field='" + field + '\'' +
          ", filterValue='" + filterValue + '\'' +
          ", relation=" + relation +
          '}';
    }
}
