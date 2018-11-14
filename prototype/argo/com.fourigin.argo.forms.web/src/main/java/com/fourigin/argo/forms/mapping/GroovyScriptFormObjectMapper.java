package com.fourigin.argo.forms.mapping;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import de.huxhorn.sulky.groovy.GroovyInstance;
import groovy.lang.Binding;
import groovy.lang.Script;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class GroovyScriptFormObjectMapper implements FormObjectMapper {
    private GroovyInstance groovyInstance;

    private CustomerRepository customerRepository;

    @Override
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void initialize(Map<String, Object> settings) {
        Objects.requireNonNull(settings, "settings must not be null!");

        String scriptPath = (String) settings.get("file");
        if (scriptPath == null) {
            throw new IllegalStateException("No 'file' settings argument present!");
        }
        File scriptFile = new File(scriptPath);
        if(!scriptFile.exists()){
            throw new IllegalArgumentException("No script file found for '" + scriptFile.getAbsolutePath() + "'!");
        }

        groovyInstance = new GroovyInstance();
        groovyInstance.setGroovyFileName(scriptPath);
    }

    @Override
    public <T> T parseValue(Class<T> targetClass, FormsStoreEntry entry) {
        Script script = groovyInstance.getInstanceAs(Script.class);
        if(script == null){
            throw new IllegalArgumentException("No script available from '" + groovyInstance.getGroovyFileName() + "'!");
        }

        Binding binding = new Binding(entry.getData());
        binding.setVariable("customerRepository", customerRepository);
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
