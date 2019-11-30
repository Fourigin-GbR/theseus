package com.fourigin.argo.controller.editors.models;

import com.fourigin.argo.models.action.Action;
import com.fourigin.argo.models.action.ActionType;

import java.util.Map;
import java.util.Objects;

public class ActionMove implements Action {
    private static final long serialVersionUID = 7007489136647443364L;

    private BaseAction base;

    private String originPath;

    private String targetFolderPath;

    private int insertionPath;

    public ActionMove(String id, long timestamp) {
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

    public int getInsertionPath() {
        return insertionPath;
    }

    public void setInsertionPath(int insertionPath) {
        this.insertionPath = insertionPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionMove that = (ActionMove) o;
        return insertionPath == that.insertionPath &&
                Objects.equals(base, that.base) &&
                Objects.equals(originPath, that.originPath) &&
                Objects.equals(targetFolderPath, that.targetFolderPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, originPath, targetFolderPath, insertionPath);
    }

    @Override
    public String toString() {
        return "ActionMove{" +
                "base=" + base +
                ", originPath='" + originPath + '\'' +
                ", targetFolderPath='" + targetFolderPath + '\'' +
                ", insertionPath=" + insertionPath +
                '}';
    }
}
