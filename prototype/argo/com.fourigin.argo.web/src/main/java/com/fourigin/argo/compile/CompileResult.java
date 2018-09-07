package com.fourigin.argo.compile;

import com.fourigin.argo.template.engine.ProcessingMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompileResult implements Serializable {
    private static final long serialVersionUID = 5351393698227068298L;

    private ProcessingMode processingMode;
    private boolean success;
    private String message;
    private Map<String, Object> attributes;

    public CompileResult(ProcessingMode processingMode, boolean success) {
        this.processingMode = processingMode;
        this.success = success;
    }

    public CompileResult(ProcessingMode processingMode, boolean success, String message) {
        this.processingMode = processingMode;
        this.success = success;
        this.message = message;
    }

    public CompileResult(ProcessingMode processingMode, boolean success, String message, Map<String, Object> attributes) {
        this.processingMode = processingMode;
        this.success = success;
        this.message = message;
        this.attributes = attributes;
    }

    public CompileResult withAttribute(String name, Object value){
        Objects.requireNonNull(name, "name must not be null!");
        Objects.requireNonNull(value, "value must not be null!");

        if(attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(name, value);
        return this;
    }

    public ProcessingMode getProcessingMode() {
        return processingMode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompileResult)) return false;
        CompileResult that = (CompileResult) o;
        return success == that.success &&
            processingMode == that.processingMode &&
            Objects.equals(message, that.message) &&
            Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processingMode, success, message, attributes);
    }

    @Override
    public String toString() {
        return "CompileResult{" +
            "processingMode=" + processingMode +
            ", success=" + success +
            ", message='" + message + '\'' +
            ", attributes=" + attributes +
            '}';
    }
}
