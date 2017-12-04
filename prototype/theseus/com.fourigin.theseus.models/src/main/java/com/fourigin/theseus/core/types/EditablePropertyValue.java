package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.PropertyAvailability;

import java.util.Objects;

public class EditablePropertyValue<T> implements PropertyValue, Comparable<T> {
    private PropertyAvailability availability;
    private T value;

    public EditablePropertyValue(PropertyAvailability availability, T value) {
        this.availability = availability;
        this.value = value;
    }

    public PropertyAvailability getAvailability() {
        return availability;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int compareTo(T o) {
        Objects.requireNonNull(o, "o must not be null!");

        if(!value.getClass().isAssignableFrom(Comparable.class)){
            throw new UnsupportedOperationException("Values of class '" + o.getClass().getName() + "' are not comparable!");
        }

        //noinspection unchecked
        Comparable<T> comparableValue = (Comparable<T>) this.value;

        return comparableValue.compareTo(o);
    }
}
