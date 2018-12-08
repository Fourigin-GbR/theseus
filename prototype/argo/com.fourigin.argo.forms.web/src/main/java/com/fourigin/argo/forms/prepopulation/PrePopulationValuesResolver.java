package com.fourigin.argo.forms.prepopulation;

import java.util.Map;

public interface PrePopulationValuesResolver {
    boolean isAccepted(PrePopulationType type);
    String getName();
    Map<String, String> resolveValues(Object context);
}
