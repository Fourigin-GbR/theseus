package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.PropertyAvailability;

public class AvailablePropertyValue implements PropertyValue {
    private PropertyAvailability availability;

    public AvailablePropertyValue(PropertyAvailability availability) {
        this.availability = availability;
    }

    public PropertyAvailability getAvailability() {
        return availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailablePropertyValue)) return false;

        AvailablePropertyValue that = (AvailablePropertyValue) o;

        return availability == that.availability;
    }

    @Override
    public int hashCode() {
        return availability != null ? availability.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AvailablePropertyValue{" +
            "availability=" + availability +
            '}';
    }
}
