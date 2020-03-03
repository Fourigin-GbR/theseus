package com.fourigin.argo.models.action.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.models.action.ActionCreate;
import com.fourigin.argo.models.action.ActionDelete;
import com.fourigin.argo.models.action.ActionMove;
import com.fourigin.argo.models.action.ActionUpdate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionCreate.class, name = "create"),
        @JsonSubTypes.Type(value = ActionUpdate.class, name = "update"),
        @JsonSubTypes.Type(value = ActionDelete.class, name = "delete"),
        @JsonSubTypes.Type(value = ActionMove.class, name = "move")
})
@SuppressWarnings({
        "PMD.AbstractClassWithoutAnyMethod",
        "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class ActionMixin {
}
