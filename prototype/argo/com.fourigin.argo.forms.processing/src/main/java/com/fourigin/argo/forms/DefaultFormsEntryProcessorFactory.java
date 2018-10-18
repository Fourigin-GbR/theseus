package com.fourigin.argo.forms;

import java.util.Map;

public class DefaultFormsEntryProcessorFactory implements FormsEntryProcessorFactory {
    private Map<String, FormsEntryProcessor> processors;

    public DefaultFormsEntryProcessorFactory(Map<String, FormsEntryProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public FormsEntryProcessor getInstance(String name) {
        return processors.get(name);
    }
}
