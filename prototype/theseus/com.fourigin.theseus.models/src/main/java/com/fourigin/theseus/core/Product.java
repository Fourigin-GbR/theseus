package com.fourigin.theseus.core;

import java.util.Objects;
import java.util.Set;

public class Product {
    private String code;
    private String referenceCode;
    private Translation name;
    private Translation description;
    private ProductType productType;
    private Price price;
    private Set<String> classifications;

    public Product(String code){
        Objects.requireNonNull(code, "code must not be null!");

        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Translation getName() {
        return name;
    }

    public void setName(Translation name) {
        this.name = name;
    }

    public Translation getDescription() {
        return description;
    }

    public void setDescription(Translation description) {
        this.description = description;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Set<String> getClassifications() {
        return classifications;
    }

    public void setClassifications(Set<String> classifications) {
        this.classifications = classifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (!code.equals(product.code)) return false;
        if (referenceCode != null ? !referenceCode.equals(product.referenceCode) : product.referenceCode != null)
            return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (description != null ? !description.equals(product.description) : product.description != null) return false;
        if (productType != product.productType) return false;
        //noinspection SimplifiableIfStatement
        if (price != null ? !price.equals(product.price) : product.price != null) return false;
        return classifications != null ? classifications.equals(product.classifications) : product.classifications == null;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + (referenceCode != null ? referenceCode.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (productType != null ? productType.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (classifications != null ? classifications.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
            "code='" + code + '\'' +
            ", referenceCode='" + referenceCode + '\'' +
            ", name=" + name +
            ", description=" + description +
            ", productType=" + productType +
            ", price=" + price +
            ", classifications=" + classifications +
            '}';
    }
}
