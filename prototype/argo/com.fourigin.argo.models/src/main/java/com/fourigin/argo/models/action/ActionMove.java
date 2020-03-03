package com.fourigin.argo.models.action;

import java.util.Map;
import java.util.Objects;

public class ActionMove implements Action {
    private static final long serialVersionUID = 7007489136647443364L;

    private String id;

    private long timestamp;

    private Map<String, Object> attributes;

    private String originPath;

    private String targetFolderPath;

    private String insertionPath;

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

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getTargetFolderPath() {
        return targetFolderPath;
    }

    public void setTargetFolderPath(String targetFolderPath) {
        this.targetFolderPath = targetFolderPath;
    }

    public String getInsertionPath() {
        return insertionPath;
    }

    public void setInsertionPath(String insertionPath) {
        this.insertionPath = insertionPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionMove that = (ActionMove) o;
        return timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(originPath, that.originPath) &&
                Objects.equals(targetFolderPath, that.targetFolderPath) &&
                Objects.equals(insertionPath, that.insertionPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, attributes, originPath, targetFolderPath, insertionPath);
    }

    @Override
    public String toString() {
        return "ActionMove{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", attributes=" + attributes +
                ", originPath='" + originPath + '\'' +
                ", targetFolderPath='" + targetFolderPath + '\'' +
                ", insertionPath='" + insertionPath + '\'' +
                '}';
    }
}
