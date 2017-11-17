package com.fourigin.theseus.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Translation extends HashMap<String, String> implements Serializable {
    private static final long serialVersionUID = 986154450235464014L;

    public Translation(){}

    public Translation(Map<String, String> values){
        Objects.requireNonNull(values, "values must not be null!");

        putAll(new HashMap<>(values));
    }

    public String getTranslation(String language){
        Objects.requireNonNull(language, "language must not be null!");

        return get(language);
    }

    public void setTranslation(String language, String value){
        Objects.requireNonNull(language, "language must not be null!");
        Objects.requireNonNull(value, "value must not be null!");

        put(language, value);
    }
}
