package com.fourigin.argo.forms.definition;

import com.fourigin.utilities.reflection.InitializableObjectDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormDefinition implements Serializable {

    private static final long serialVersionUID = -1574891219507468279L;

    private String form;
    private Map<String, ValidationPattern> validationPatterns;
    private Map<String, InitializableObjectDescriptor> dataNormalizers;
    private Map<String, InitializableObjectDescriptor> dataFormatters;
    private List<ProcessingStage> stages;
    private Map<String, FieldDefinition> fields;
    private Map<String, InitializableObjectDescriptor> objectMappings;

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Map<String, ValidationPattern> getValidationPatterns() {
        return validationPatterns;
    }

    public void setValidationPatterns(Map<String, ValidationPattern> validationPatterns) {
        this.validationPatterns = validationPatterns;
    }

    public Map<String, InitializableObjectDescriptor> getDataNormalizers() {
        return dataNormalizers;
    }

    public void setDataNormalizers(Map<String, InitializableObjectDescriptor> dataNormalizers) {
        this.dataNormalizers = dataNormalizers;
    }

    public Map<String, InitializableObjectDescriptor> getDataFormatters() {
        return dataFormatters;
    }

    public void setDataFormatters(Map<String, InitializableObjectDescriptor> dataFormatters) {
        this.dataFormatters = dataFormatters;
    }

    public List<ProcessingStage> getStages() {
        return stages;
    }

    public void setStages(List<ProcessingStage> stages) {
        this.stages = stages;
    }

    public Map<String, FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldDefinition> fields) {
        this.fields = fields;
    }

    public Map<String, InitializableObjectDescriptor> getObjectMappings() {
        return objectMappings;
    }

    public void setObjectMappings(Map<String, InitializableObjectDescriptor> objectMappings) {
        this.objectMappings = objectMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormDefinition that = (FormDefinition) o;
        return Objects.equals(form, that.form) &&
                Objects.equals(validationPatterns, that.validationPatterns) &&
                Objects.equals(dataNormalizers, that.dataNormalizers) &&
                Objects.equals(dataFormatters, that.dataFormatters) &&
                Objects.equals(stages, that.stages) &&
                Objects.equals(fields, that.fields) &&
                Objects.equals(objectMappings, that.objectMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, validationPatterns, dataNormalizers, dataFormatters, stages, fields, objectMappings);
    }

    @Override
    public String toString() {
        return "FormDefinition{" +
                "form='" + form + '\'' +
                ", validationPatterns=" + validationPatterns +
                ", dataNormalizers=" + dataNormalizers +
                ", dataFormatters=" + dataFormatters +
                ", stages=" + stages +
                ", fields=" + fields +
                ", objectMappings=" + objectMappings +
                '}';
    }
}
