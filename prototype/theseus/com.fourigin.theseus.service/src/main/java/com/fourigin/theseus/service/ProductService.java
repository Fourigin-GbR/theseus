package com.fourigin.theseus.service;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;
import com.fourigin.theseus.repository.filter.PropertySearchFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> getInfo();
    List<String> findProductCodes(ProductSearchFilter filter);

    Product resolveProduct(String code);
    Map<String, Product> resolveProducts(Collection<String> productCodes);

    List<String> findPropertyCodes(PropertySearchFilter filter);
    List<String> findProductPropertyCodes(String productCode, PropertySearchFilter filter);

    PropertyDefinition<? extends PropertyType> resolvePropertyDefinition(String propertyCode);
    Map<String, PropertyDefinition<? extends PropertyType>> resolvePropertyDefinitions(Collection<String> propertyCodes);

    Property<? extends PropertyType> resolveProperty(String productCode, String propertyCode);
    Map<String, Property<? extends PropertyType>> resolveProperties(String productCode, Collection<String> propertyCodes);

}
