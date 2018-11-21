package com.fourigin.utilities.reflection;

import java.util.Collection;
import java.util.Map;

public interface Initializable {
    Collection<String> requiredKeys();
    void initialize(Map<String, Object> settings);
}
