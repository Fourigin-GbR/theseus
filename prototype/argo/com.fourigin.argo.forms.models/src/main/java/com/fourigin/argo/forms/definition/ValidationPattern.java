package com.fourigin.argo.forms.definition;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ValidationPattern implements Serializable {
    private static final long serialVersionUID = 3349821729222368320L;
    
    private String pattern;
    private List<String> validExamples;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getValidExamples() {
        return validExamples;
    }

    public void setValidExamples(List<String> validExamples) {
        this.validExamples = validExamples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationPattern)) return false;
        ValidationPattern that = (ValidationPattern) o;
        return Objects.equals(pattern, that.pattern) &&
            Objects.equals(validExamples, that.validExamples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, validExamples);
    }

    @Override
    public String toString() {
        return "ValidationPattern{" +
            "pattern='" + pattern + '\'' +
            ", validExamples=" + validExamples +
            '}';
    }
}
