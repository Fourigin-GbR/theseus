package com.fourigin.argo.forms.mapping;

import com.fourigin.argo.forms.models.FormsStoreEntry;

import java.util.Map;

public interface FormObjectMapper {
    void initialize(Map<String, Object> settings);

    <T> T parseValue(Class<T> targetClass, FormsStoreEntry entry);
}
