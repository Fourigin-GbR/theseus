package com.fourigin.argo.controller.editors.models;

import com.fourigin.argo.models.action.Action;
import com.fourigin.argo.models.action.ActionType;

import java.util.Map;
import java.util.Objects;

public class ActionUpdate implements Action {
    private static final long serialVersionUID = 2215922467111446191L;

    private BaseAction base;

    private String path;

    private SiteNode item;

    public ActionUpdate(String id, long timestamp) {
        this.base = new BaseAction(id, timestamp);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.UPDATE;
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
        return Objects.equals(base, that.base) &&
                Objects.equals(path, that.path) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, path, item);
    }

    @Override
    public String toString() {
        return "ActionUpdate{" +
                "base=" + base +
                ", path='" + path + '\'' +
                ", item=" + item +
                '}';
    }
}
