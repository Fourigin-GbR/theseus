package com.fourigin.argo.assets.models;

import java.util.Map;
import java.util.Set;

public interface Asset {

    String LOCALIZED_FILENAME_PREFIX = "localized-filename-prefix";
    String LOCALIZED_ALT_TEXT_PREFIX = "localized-alt-text-prefix";
    String DEFAULT_ALT_TEXT = "default-alt-text";
    String CREATED_BY_ATTRIBUTE_NAME = "created-by-user";
    String CREATION_DATE_ATTRIBUTE_NAME = "creation-date";
    String CREATION_TIMESTAMP_ATTRIBUTE_NAME = "creation-timestamp";
    String CHANGED_BY_ATTRIBUTE_NAME = "changed-by-user";
    String CHANGE_DATE_ATTRIBUTE_NAME = "changed-date";
    String CHANGE_TIMESTAMP_ATTRIBUTE_NAME = "changed-timestamp";

    String getId();
    void setId(String id);

    String getName();
    void setName(String name);

    String getMimeType();
    void setMimeType(String mimeType);

    Set<String> getTags();
    void setTags(Set<String> tags);

    Map<String, String> getAttributes();
    void setAttributes(Map<String, String> attributes);
    String getAttribute(String key);
    void setAttribute(String key, String value);
    void removeAttribute(String key);

}
