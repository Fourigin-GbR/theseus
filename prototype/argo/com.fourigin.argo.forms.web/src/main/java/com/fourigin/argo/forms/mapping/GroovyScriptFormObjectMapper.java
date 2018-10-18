package com.fourigin.argo.forms.mapping;

import com.fourigin.argo.forms.models.FormsStoreEntry;
import de.huxhorn.sulky.groovy.GroovyInstance;
import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.Map;
import java.util.Objects;

public class GroovyScriptFormObjectMapper implements FormObjectMapper {
    private GroovyInstance groovyInstance;

    @Override
    public void initialize(Map<String, Object> settings) {
        Objects.requireNonNull(settings, "settings must not be null!");

        String scriptPath = (String) settings.get("file");
        if (scriptPath == null) {
            throw new IllegalStateException("No 'file' settings argument present!");
        }

        groovyInstance = new GroovyInstance();
        groovyInstance.setGroovyFileName(scriptPath);
    }

    @Override
    public <T> T parseValue(Class<T> targetClass, FormsStoreEntry entry) {
        Script script = groovyInstance.getInstanceAs(Script.class);

        Binding binding = new Binding(entry.getData());
        script.setBinding(binding);

        Object result = script.run();
        if (result == null) {
            return null;
        }

        if (targetClass.isAssignableFrom(result.getClass())) {
            return targetClass.cast(result);
        }

        return null;
    }
}
