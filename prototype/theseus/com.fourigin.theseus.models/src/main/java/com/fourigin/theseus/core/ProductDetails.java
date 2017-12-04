package com.fourigin.theseus.core;

import com.fourigin.theseus.core.types.PropertyValue;

import java.util.Map;

public class ProductDetails {
    private Product product;

    private Map<String, PropertyValue> properties;

    public ProductDetails(Product product, Map<String, PropertyValue> properties) {
        this.product = product;
        this.properties = properties;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Map<String, PropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PropertyValue> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDetails)) return false;

        ProductDetails that = (ProductDetails) o;

        //noinspection SimplifiableIfStatement
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductDetails{" +
            "product=" + product +
            ", properties=" + properties +
            '}';
    }
}
