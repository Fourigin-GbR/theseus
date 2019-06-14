package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationMessage implements Serializable {
    private static final long serialVersionUID = 6777567224670959531L;

    private String validator;
    private String code;
    private String formattedMessage;
    private List<String> arguments;

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        if (!(o instanceof ValidationMessage)) return false;
        ValidationMessage that = (ValidationMessage) o;
        return Objects.equals(validator, that.validator) &&
            Objects.equals(code, that.code) &&
            Objects.equals(formattedMessage, that.formattedMessage) &&
            Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validator, code, formattedMessage, arguments);
    }

    @Override
    public String toString() {
        return "ValidationMessage{" +
            "validator='" + validator + '\'' +
            ", code='" + code + '\'' +
            ", formattedMessage='" + formattedMessage + '\'' +
            ", arguments=" + arguments +
            '}';
    }

    static class Builder {
        private String validator;
        private String errorCode;
        private String formattedMessage;
        private List<String> arguments;

        public Builder withValidator(String validatorClass) {
            this.validator = validatorClass;
            return this;
        }

        public Builder withCode(String code) {
            this.errorCode = code;
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
            if (value != null) {
                arguments.add(value);
            }

            return this;
        }

        public ValidationMessage build() {
            ValidationMessage reason = new ValidationMessage();

            reason.setValidator(validator);
            reason.setCode(errorCode);
            reason.setFormattedMessage(formattedMessage);
            reason.setArguments(arguments);

            return reason;
        }
    }
}
