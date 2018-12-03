package com.fourigin.argo.forms.initialization;

import java.util.Map;
import java.util.Set;

public interface ExternalValueResolver {
    Set<String> getSupportedKeys();

    Map<String, InitialValue> resolveExternalValue(String customerId, String key);
}
