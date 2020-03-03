package com.fourigin.argo.models.action.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.models.action.Action;

public class ActionModule extends SimpleModule {

    private static final long serialVersionUID = -2395336522591832539L;

    public ActionModule() {
        super("Action", Version.unknownVersion());

        setMixInAnnotation(Action.class, ActionMixin.class);
//        setMixInAnnotation(ActionCreate.class, ActionMixin.class);
//        setMixInAnnotation(ActionUpdate.class, ActionMixin.class);
//        setMixInAnnotation(ActionDelete.class, ActionMixin.class);
//        setMixInAnnotation(ActionMove.class, ActionMixin.class);
    }
}