package com.fourigin.argo.forms.mapping;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import de.huxhorn.sulky.groovy.GroovyInstance;
import groovy.lang.Binding;
import groovy.lang.Script;

import java.io.File;
import java.util.Objects;

public class FormObjectMapper {
    private GroovyInstance groovyInstance;

    private CustomerRepository customerRepository;
    private FormsStoreRepository formsStoreRepository;

    public FormObjectMapper(CustomerRepository customerRepository, FormsStoreRepository formsStoreRepository, String scriptPath) {
        this.customerRepository = customerRepository;
        this.formsStoreRepository = formsStoreRepository;
        initialize(scriptPath);
    }

    private void initialize(String scriptPath) {
        Objects.requireNonNull(scriptPath, "scriptPath must not be null!");

        File scriptFile = new File(scriptPath);
        if(!scriptFile.exists()){
            throw new IllegalArgumentException("No script file found for '" + scriptFile.getAbsolutePath() + "'!");
        }

        groovyInstance = new GroovyInstance();
        groovyInstance.setGroovyFileName(scriptPath);
    }

    public <T> T parseValue(Class<T> targetClass, FormsStoreEntry entry, String entryId) {
        Script script = groovyInstance.getInstanceAs(Script.class);
        if(script == null){
            throw new IllegalArgumentException("No script available from '" + groovyInstance.getGroovyFileName() + "'!");
        }

        Binding binding = new Binding(entry.getData());
        binding.setVariable("id", entryId);
        binding.setVariable("formsStoreRepository", formsStoreRepository);
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
