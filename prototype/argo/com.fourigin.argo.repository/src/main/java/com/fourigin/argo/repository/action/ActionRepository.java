package com.fourigin.argo.repository.action;

import com.fourigin.argo.models.action.Action;

import java.util.List;

public interface ActionRepository {
    void addAction(String revision, Action action);
    List<Action> resolveDiff(String fromRevision, String toRevision);
}
