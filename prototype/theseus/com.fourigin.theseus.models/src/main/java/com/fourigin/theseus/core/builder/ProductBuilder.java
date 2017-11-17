package com.fourigin.theseus.core.builder;


import com.fourigin.theseus.core.Price;
import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.core.ProductType;
import com.fourigin.theseus.core.Translation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProductBuilder {
    private String code;
    private String referenceCode;
    private Translation name = new Translation();
    private Translation description = new Translation();
    private ProductType productType = ProductType.PRODUCT;
    private Price price;
    private Set<String> classifications;

    public ProductBuilder(){
        reset();
    }

    public ProductBuilder code(String code){
        this.code = code;

        return this;
    }

    public ProductBuilder referenceCode(String referenceCode){
        this.referenceCode = referenceCode;

        return this;
    }

    public ProductBuilder name(String language, String name){
        Objects.requireNonNull(language, "language must not be null!");
        Objects.requireNonNull(name, "name must not be null!");

        this.name.setTranslation(language, name);

        return this;
    }

    public ProductBuilder description(String language, String description){
        Objects.requireNonNull(language, "language must not be null!");
        Objects.requireNonNull(description, "description must not be null!");

        this.description.setTranslation(language, description);

        return this;
    }

    public ProductBuilder productType(ProductType productType){
        this.productType = productType;

        return this;
    }

    public ProductBuilder priceValue(String currency, double value){
        Objects.requireNonNull(currency, "currency must not be null!");

        if(price == null) {
            price = new Price(currency, value);
            return this;
        }

        price.setPrice(currency, value);

        return this;
    }

    public ProductBuilder price(Price price){
        Objects.requireNonNull(price, "price must not be null!");

        this.price = price;

        return this;
    }

    public ProductBuilder classification(String classification){
        Objects.requireNonNull(classification, "classification must not be null!");

        if(classifications == null){
            classifications = new HashSet<>();
        }

        classifications.add(classification);

        return this;
    }

    public ProductBuilder reset(){
        code = null;
        referenceCode = null;
        name = new Translation();
        description = new Translation();
        productType = ProductType.PRODUCT;
        price = null;
        classifications = null;

        return this;
    }

    public Product build(){
        if(code == null){
            throw new IllegalArgumentException("code must not be null!");
        }

        Product product = new Product(code);
        product.setProductType(productType);

        if(referenceCode != null && !referenceCode.isEmpty()) {
            product.setReferenceCode(referenceCode);
        }

        if(name != null) {
            product.setName(name);
        }

        if(description != null) {
            product.setDescription(description);
        }

        if(price != null) {
            product.setPrice(price);
        }

        if(classifications != null) {
            product.setClassifications(classifications);
        }
        
        return product;
    }
}
