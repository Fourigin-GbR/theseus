package com.fourigin.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocalizedText {
    private Map<String, String> values;

    public void setValue(String language, String value){
        if(values == null){
            values = new HashMap<>();
        }

        values.put(language, value);
    }

    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizedText)) return false;
        LocalizedText that = (LocalizedText) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "LocalizedText{" +
            "values=" + values +
            '}';
    }
}
