package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.repository.filter.PropertySearchFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PropertyRepositoryStub implements PropertyRepository {
    private Set<String> allPropertyCodes;

    private Map<String, Set<String>> propertyCodesByProductCode;

    private Map<String, PropertyDefinition<? extends PropertyType>> definitions;

    private Map<String, Map<String, Property<? extends PropertyType>>> propertiesByProductCode;

    public PropertyRepositoryStub() {
    }

    @Override
    public List<String> findPropertyCodes(PropertySearchFilter filter) {
        if(allPropertyCodes == null){
            return null;
        }

        List<String> result = new ArrayList<>(allPropertyCodes);
        result.sort(String::compareTo);
        return result;
    }

    @Override
    public List<String> findProductPropertyCodes(String productCode, PropertySearchFilter filter) {
        if(propertyCodesByProductCode == null){
            return null;
        }

        Set<String> productCodes = propertyCodesByProductCode.get(productCode);
        if(productCodes == null){
            return null;
        }

        List<String> result = new ArrayList<>(productCodes);
        result.sort(String::compareTo);
        return result;
    }

    @Override
    public PropertyDefinition<? extends PropertyType> resolvePropertyDefinition(String propertyCode) {
        return definitions.get(propertyCode);
    }

    @Override
    public Map<String, PropertyDefinition<? extends PropertyType>> resolvePropertyDefinitions(Collection<String> propertyCodes) {
        Map<String, PropertyDefinition<? extends PropertyType>> result = new HashMap<>();

        for (String propertyCode : propertyCodes) {
            result.put(propertyCode, definitions.get(propertyCode));
        }

        return result;
    }

    @Override
    public Property<? extends PropertyType> resolveProperty(String productCode, String propertyCode) {
        Map<String, Property<? extends PropertyType>> productProperties = propertiesByProductCode.get(productCode);
        if(productProperties == null){
            return null;
        }

        return productProperties.get(propertyCode);
    }

    @Override
    public Map<String, Property<? extends PropertyType>> resolveProperties(String productCode, Collection<String> propertyCodes) {
        Map<String, Property<? extends PropertyType>> productProperties = propertiesByProductCode.get(productCode);
        if(productProperties == null){
            return null;
        }

        Map<String, Property<? extends PropertyType>> result = new HashMap<>();

        for (String propertyCode : propertyCodes) {
            result.put(propertyCode, productProperties.get(propertyCode));
        }

        return result;
    }

    @Override
    public void createPropertyDefinition(PropertyDefinition<? extends PropertyType> definition) {
        String propertyCode = definition.getCode();

        if(definitions == null){
            definitions = new HashMap<>();
        }

        if(allPropertyCodes == null){
            allPropertyCodes = new HashSet<>();
        }

        allPropertyCodes.add(propertyCode);

        definitions.put(propertyCode, definition);
    }

    @Override
    public void updatePropertyDefinition(PropertyDefinition<? extends PropertyType> definition) {
        if(definitions == null) {
            throw new IllegalStateException("No definitions available, nothing to update!");
        }

        definitions.put(definition.getCode(), definition);
    }

    @Override
    public void deletePropertyDefinition(String propertyCode) {
        if(definitions == null) {
            throw new IllegalStateException("No definitions available, nothing to delete!");
        }

        if(allPropertyCodes != null){
            allPropertyCodes.remove(propertyCode);
        }

        definitions.remove(propertyCode);
    }

    @Override
    public void createProductProperty(String productCode, Property<? extends PropertyType> property) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(property, "property must not be null!");

        if(allPropertyCodes == null){
            allPropertyCodes = new HashSet<>();
        }

        if(propertiesByProductCode == null){
            propertiesByProductCode = new HashMap<>();
        }

        Map<String, Property<? extends PropertyType>> productProperties = propertiesByProductCode.get(productCode);
        if(productProperties == null){
            productProperties = new HashMap<>();
            propertiesByProductCode.put(productCode, productProperties);
        }

        if(propertyCodesByProductCode == null){
            propertyCodesByProductCode = new HashMap<>();
        }

        Set<String> productPropertyCodes = propertyCodesByProductCode.get(productCode);
        if(productPropertyCodes == null){
            productPropertyCodes = new HashSet<>();
            propertyCodesByProductCode.put(productCode, productPropertyCodes);
        }

        PropertyDefinition<? extends PropertyType> definition = property.getDefinition();
        Objects.requireNonNull(definition, "property definition must not be null!");

        String propertyCode = definition.getCode();

        allPropertyCodes.add(propertyCode);
        productPropertyCodes.add(propertyCode);
        productProperties.put(propertyCode, property);
    }

    @Override
    public void updateProductProperty(String productCode, Property<? extends PropertyType> property) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(property, "property must not be null!");

        if(propertiesByProductCode == null){
            throw new IllegalStateException("No properties available at all, nothing to update!");
        }

        Map<String, Property<? extends PropertyType>> productProperties = propertiesByProductCode.get(productCode);
        if(productProperties == null){
            throw new IllegalStateException("No properties available for product '" + productCode + "', nothing to update!");
        }

        PropertyDefinition<? extends PropertyType> definition = property.getDefinition();
        Objects.requireNonNull(definition, "property definition must not be null!");

        productProperties.put(definition.getCode(), property);
    }

    @Override
    public void deleteProductProperty(String productCode, String propertyCode) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(propertyCode, "propertyCode must not be null!");

        if(propertiesByProductCode == null){
            throw new IllegalStateException("No properties available at all, nothing to delete!");
        }

        Map<String, Property<? extends PropertyType>> productProperties = propertiesByProductCode.get(productCode);
        if(productProperties == null){
            throw new IllegalStateException("No properties available for product '" + productCode + "', nothing to delete!");
        }

        if(propertyCodesByProductCode == null){
            throw new IllegalStateException("No properties available at all, nothing to delete!");
        }

        Set<String> productPropertyCodes = propertyCodesByProductCode.get(productCode);
        if(productPropertyCodes == null){
            throw new IllegalStateException("No properties available for product '" + productCode + "', nothing to delete!");
        }

        productProperties.remove(propertyCode);
        if(productProperties.isEmpty()){
            propertiesByProductCode.remove(productCode);
        }

        productPropertyCodes.remove(propertyCode);
        if(productPropertyCodes.isEmpty()){
            propertyCodesByProductCode.remove(productCode);
        }
    }
}
