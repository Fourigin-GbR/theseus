package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.types.PropertyType;

public interface PropertyRepository extends PropertyResolver {
    void createPropertyDefinition(PropertyDefinition<? extends PropertyType> definition);
    void updatePropertyDefinition(PropertyDefinition<? extends PropertyType> definition);
    void deletePropertyDefinition(String propertyCode);

    void createProductProperty(String productCode, Property<? extends PropertyType> property);
    void updateProductProperty(String productCode, Property<? extends PropertyType> property);
    void deleteProductProperty(String productCode, String propertyCode);
}
