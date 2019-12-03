package com.fourigin.argo.controller.editors.models;

import com.fourigin.argo.models.action.Action;
import com.fourigin.argo.models.action.ActionType;

import java.util.Map;
import java.util.Objects;

public class ActionDelete implements Action {
    private static final long serialVersionUID = 9050120449829139735L;

    private BaseAction base;

    private String path;

    public ActionDelete(String id, long timestamp) {
        this.base = new BaseAction(id, timestamp);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE;
    }

    @Override
    public String getId() {
        return base.getId();
    }

    @Override
    public long getTimestamp() {
        return base.getTimestamp();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return base.getAttributes();
    }

    @Override
    public void addAttribute(String key, Object value) {
        base.addAttribute(key, value);
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
        return Objects.equals(base, that.base) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, path);
    }

    @Override
    public String toString() {
        return "ActionDelete{" +
                "base=" + base +
                ", path='" + path + '\'' +
                '}';
    }
}
