package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.Price;
import com.fourigin.theseus.core.PropertyAvailability;

public class SetPropertyValueEntry {
    private PropertyAvailability availability;
    private Price price;

    public SetPropertyValueEntry(PropertyAvailability availability) {
        this.availability = availability;
        this.price = null;
    }

    public SetPropertyValueEntry(PropertyAvailability availability, Price price) {
        this.availability = availability;
        this.price = price;
    }

    public PropertyAvailability getAvailability() {
        return availability;
    }

    public Price getPrice() {
        return price;
    }
}
