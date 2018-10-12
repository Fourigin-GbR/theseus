package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.FormDefinition;

public interface ExternalValueResolver {
    void resolveValues(FormDefinition formDefinition);
}
