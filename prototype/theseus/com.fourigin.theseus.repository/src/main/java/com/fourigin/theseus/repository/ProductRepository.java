package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Product;

public interface ProductRepository extends ProductResolver {
    void createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(String productCode);
}
