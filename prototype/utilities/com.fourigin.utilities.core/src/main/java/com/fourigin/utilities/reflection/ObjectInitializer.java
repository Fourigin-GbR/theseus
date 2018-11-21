package com.fourigin.utilities.reflection;

import java.util.Map;
import java.util.Objects;

public class ObjectInitializer {

    public static <T extends Initializable> T initialize(InitializableObjectDescriptor descriptor) {
        String targetClassName = descriptor.getTargetClass();
        try {
            //noinspection unchecked
            Class<T> targetClass = (Class<T>) Class.forName(targetClassName);
            return initialize(targetClass, descriptor.getSettings());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Unable to instantiate class for name '" + targetClassName + "'!", ex);
        }
    }

    public static <T extends Initializable> T initialize(Class<T> target, Map<String, Object> settings){
        Objects.requireNonNull(target, "target must not be null!");

        T instance;
        try {
            instance = target.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Unable to instantiate class for name '" + target.getName() + "'!", ex);
        }

        if(settings != null && !settings.isEmpty()) {
            instance.initialize(settings);
        }
        
        return instance;
    }
}
