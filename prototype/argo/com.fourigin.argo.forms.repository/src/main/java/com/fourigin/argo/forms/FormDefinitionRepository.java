package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.util.List;

public interface FormDefinitionRepository {
    List<String> listDefinitionIds();
    FormDefinition retrieve(String formDefinitionId);
    void create(FormDefinition formDefinition);
    void update(FormDefinition formDefinition);
    void delete(String formDefinitionId);
}
