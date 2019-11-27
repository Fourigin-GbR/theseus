package com.fourigin.argo.controller.editors.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BaseAction {
    private String id;

    private long timestamp;

    private Map<String, Object> attributes;

    public BaseAction(String id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null!");

        if (value == null && attributes != null) {
            attributes.remove(key);
            return;
        }

        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }
}
