package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FailureReason implements Serializable {
    private static final long serialVersionUID = 6777567224670959531L;

    private String validator;
    private String failureCode;
    private String formattedMessage;
    private List<String> arguments;

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

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailureReason)) return false;
        FailureReason reason = (FailureReason) o;
        return Objects.equals(validator, reason.validator) &&
            Objects.equals(failureCode, reason.failureCode) &&
            Objects.equals(formattedMessage, reason.formattedMessage) &&
            Objects.equals(arguments, reason.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validator, failureCode, formattedMessage, arguments);
    }

    @Override
    public String toString() {
        return "FailureReason{" +
            "validator='" + validator + '\'' +
            ", failureCode='" + failureCode + '\'' +
            ", formattedMessage='" + formattedMessage + '\'' +
            ", arguments=" + arguments +
            '}';
    }

    static class Builder {
        private String validator;
        private String failureCode;
        private String formattedMessage;
        private List<String> arguments;

        public Builder withValidator(String validatorClass) {
            this.validator = validatorClass;
            return this;
        }

        public Builder withCode(String failureCode) {
            this.failureCode = failureCode;
            return this;
        }

        public Builder withFormattedMessage(String formattedMessage) {
            this.formattedMessage = formattedMessage;
            return this;
        }

        public Builder withArgument(String value) {
            if (arguments == null) {
                arguments = new ArrayList<>();
            }
            if(value != null) {
                arguments.add(value);
            }

            return this;
        }

        public FailureReason build() {
            FailureReason reason = new FailureReason();

            reason.setValidator(validator);
            reason.setFailureCode(failureCode);
            reason.setFormattedMessage(formattedMessage);
            reason.setArguments(arguments);

            return reason;
        }
    }
}
