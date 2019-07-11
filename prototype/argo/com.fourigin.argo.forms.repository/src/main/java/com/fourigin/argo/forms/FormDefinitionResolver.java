package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.definition.ProcessingStages;

import java.util.List;

public interface FormDefinitionResolver {
    List<String> listDefinitionIds();
    ProcessingStages retrieveStages(String formDefinitionId);
    FormDefinition retrieveDefinition(String formDefinitionId, String stageName);
}
