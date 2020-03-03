package com.fourigin.argo.models.action;

import java.io.Serializable;
import java.util.Map;

public interface Action extends Serializable {
    String getId();

    long getTimestamp();

    ActionType getActionType();

    Map<String, Object> getAttributes();
}
