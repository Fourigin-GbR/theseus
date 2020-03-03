package com.fourigin.argo.models.action;

import java.util.Map;
import java.util.Objects;

public class ActionDelete implements Action {
    private static final long serialVersionUID = 9050120449829139735L;

    private String id;

    private long timestamp;

    private Map<String, Object> attributes;

    private String path;

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE;
    }

//    public void addAttribute(String key, Object value) {
//        Objects.requireNonNull(key, "key must not be null!");
//
//        if (value == null && attributes != null) {
//            attributes.remove(key);
//            return;
//        }
//
//        if (attributes == null) {
//            attributes = new HashMap<>();
//        }
//        attributes.put(key, value);
//    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionDelete that = (ActionDelete) o;
        return timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, attributes, path);
    }

    @Override
    public String toString() {
        return "ActionDelete{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", attributes=" + attributes +
                ", path='" + path + '\'' +
                '}';
    }
}
