package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FailureReason implements Serializable {
    private static final long serialVersionUID = 6777567224670959531L;

    private String validator;
    private String failureCode;
    private Map<String, String> arguments;

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailureReason)) return false;
        FailureReason that = (FailureReason) o;
        return Objects.equals(validator, that.validator) &&
            Objects.equals(failureCode, that.failureCode) &&
            Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validator, failureCode, arguments);
    }

    @Override
    public String toString() {
        return "FailureReason{" +
            "withValidator='" + validator + '\'' +
            ", failureCode='" + failureCode + '\'' +
            ", arguments=" + arguments +
            '}';
    }

    static class Builder {
        private String validator;
        private String failureCode;
        private Map<String, String> arguments;

        public Builder withValidator(String validatorClass) {
            this.validator = validatorClass;
            return this;
        }

        public Builder withCode(String failureCode) {
            this.failureCode = failureCode;
            return this;
        }

        public Builder withArgument(String key, String value) {
            if (arguments == null) {
                arguments = new HashMap<>();
            }
            arguments.put(key, value);
            return this;
        }

        public FailureReason build() {
            FailureReason reason = new FailureReason();

            reason.setValidator(validator);
            reason.setFailureCode(failureCode);
            reason.setArguments(arguments);

            return reason;
        }
    }
}
