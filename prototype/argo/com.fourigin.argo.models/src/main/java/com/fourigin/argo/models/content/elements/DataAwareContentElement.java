package com.fourigin.argo.models.content.elements;

public interface DataAwareContentElement {
    String getContent();
    void setContent(String value);

    DataType getDataType();
    void setDataType(DataType dataType);
}
