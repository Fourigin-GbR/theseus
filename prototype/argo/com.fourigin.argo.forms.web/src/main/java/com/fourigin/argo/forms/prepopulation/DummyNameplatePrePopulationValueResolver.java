package com.fourigin.argo.forms.prepopulation;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class DummyNameplatePrePopulationValueResolver implements PrePopulationValuesResolver {
    private String resolverName;

    public DummyNameplatePrePopulationValueResolver(String resolverName) {
        this.resolverName = resolverName;
    }

    @Override
    public boolean isAccepted(PrePopulationType type) {
        return PrePopulationType.FORM_DEFINITION == type;
    }

    @Override
    public String getName() {
        return resolverName;
    }

    @Override
    public Map<String, String> resolveValues(Object context) {
        Objects.requireNonNull(context, "context must not be null!");

        if (!FormDefinition.class.isAssignableFrom(context.getClass())) {
            throw new IllegalArgumentException("Incompatible class for an FORM_DEFINITION-Type-Resolver! Required context of type " + FormDefinition.class + ", but found " + context.getClass());
        }

        FormDefinition formDefinition = (FormDefinition) context;

        if ("register-vehicle".equals(formDefinition.getForm())) {
            return Collections.singletonMap("default-nameplate", "DA-VN 757");
        }

        return null;
    }
}
