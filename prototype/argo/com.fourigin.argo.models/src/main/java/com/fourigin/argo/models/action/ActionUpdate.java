package com.fourigin.argo.models.action;

import java.util.Map;
import java.util.Objects;

public class ActionUpdate implements Action {
    private static final long serialVersionUID = 2215922467111446191L;

    private String id;

    private long timestamp;

    private Map<String, Object> attributes;

    private String path;

    private SiteNode item;

    @Override
    public ActionType getActionType() {
        return ActionType.UPDATE;
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

    public SiteNode getItem() {
        return item;
    }

    public void setItem(SiteNode item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionUpdate that = (ActionUpdate) o;
        return timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(path, that.path) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, attributes, path, item);
    }

    @Override
    public String toString() {
        return "ActionUpdate{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", attributes=" + attributes +
                ", path='" + path + '\'' +
                ", item=" + item +
                '}';
    }
}
