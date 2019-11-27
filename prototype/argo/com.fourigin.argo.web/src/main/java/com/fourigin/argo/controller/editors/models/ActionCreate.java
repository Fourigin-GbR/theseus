package com.fourigin.argo.controller.editors.models;

import com.fourigin.argo.models.action.Action;
import com.fourigin.argo.models.action.ActionType;

import java.util.Map;
import java.util.Objects;

public class ActionCreate implements Action {
    private static final long serialVersionUID = -7315915431136801730L;

    private BaseAction base;

    private String folderPath;

    private int targetPosition;

    private SiteNode item;

    public ActionCreate(String id, long timestamp) {
        this.base = new BaseAction(id, timestamp);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CREATE;
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

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
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
        ActionCreate that = (ActionCreate) o;
        return targetPosition == that.targetPosition &&
                Objects.equals(base, that.base) &&
                Objects.equals(folderPath, that.folderPath) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, folderPath, targetPosition, item);
    }

    @Override
    public String toString() {
        return "ActionCreate{" +
                "base=" + base +
                ", folderPath='" + folderPath + '\'' +
                ", targetPosition=" + targetPosition +
                ", item=" + item +
                '}';
    }
}
