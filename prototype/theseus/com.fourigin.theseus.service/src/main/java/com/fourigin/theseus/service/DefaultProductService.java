package com.fourigin.theseus.service;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.repository.ProductRepository;
import com.fourigin.theseus.repository.PropertyRepository;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;
import com.fourigin.theseus.repository.filter.PropertySearchFilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultProductService implements ProductService {
    private ProductRepository productRepository;

    private PropertyRepository propertyRepository;

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> productsInfo = productRepository.getInfo();
        if(productsInfo != null){
            result.putAll(productsInfo);
        }

        Map<String, Object> propertiesInfo = propertyRepository.getInfo();
        if(propertiesInfo != null){
            result.putAll(propertiesInfo);
        }
        
        return result;
    }

    @Override
    public List<String> findProductCodes(ProductSearchFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null!");

        return productRepository.findProductCodes(filter);
    }

    @Override
    public Product resolveProduct(String code) {
        Objects.requireNonNull(code, "code must not be null!");

        return productRepository.resolveProduct(code);
    }

    @Override
    public Map<String, Product> resolveProducts(Collection<String> productCodes) {
        Objects.requireNonNull(productCodes, "productCodes must not be null!");

        return productRepository.resolveProducts(productCodes);
    }

    @Override
    public List<String> findPropertyCodes(PropertySearchFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null!");

        return propertyRepository.findPropertyCodes(filter);
    }

    @Override
    public List<String> findProductPropertyCodes(String productCode, PropertySearchFilter filter) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(filter, "filter must not be null!");

        return propertyRepository.findProductPropertyCodes(productCode, filter);
    }

    @Override
    public PropertyDefinition<? extends PropertyType> resolvePropertyDefinition(String propertyCode) {
        Objects.requireNonNull(propertyCode, "propertyCode must not be null!");

        return propertyRepository.resolvePropertyDefinition(propertyCode);
    }

    @Override
    public Map<String, PropertyDefinition<? extends PropertyType>> resolvePropertyDefinitions(Collection<String> propertyCodes) {
        Objects.requireNonNull(propertyCodes, "propertyCodes must not be null!");

        return propertyRepository.resolvePropertyDefinitions(propertyCodes);
    }

    @Override
    public Property<? extends PropertyType> resolveProperty(String productCode, String propertyCode) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(propertyCode, "propertyCode must not be null!");

        return propertyRepository.resolveProperty(productCode, propertyCode);
    }

    @Override
    public Map<String, Property<? extends PropertyType>> resolveProperties(String productCode, Collection<String> propertyCodes) {
        Objects.requireNonNull(productCode, "productCode must not be null!");
        Objects.requireNonNull(propertyCodes, "propertyCodes must not be null!");

        return propertyRepository.resolveProperties(productCode, propertyCodes);
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void setPropertyRepository(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }
}
