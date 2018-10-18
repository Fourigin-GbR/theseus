package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.util.Collection;
import java.util.List;

public interface FormDefinitionRepository {
    List<String> listDefinitionIds();
    FormDefinition retrieveDefinition(String formDefinitionId);
    void createDefinition(FormDefinition formDefinition);
    void updateDefinition(FormDefinition formDefinition);
    void deleteDefinition(String formDefinitionId);

    void setExternalValueResolvers(Collection<ExternalValueResolver> resolvers);
}
