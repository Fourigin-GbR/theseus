package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepositoryStub implements ProductRepository {
    private Map<String, Product> products = new HashMap<>();

    @Override
    public List<String> findProductCodes(ProductSearchFilter filter) {
        List<String> codes = new ArrayList<>(products.keySet());
        codes.sort(String::compareTo);
        return codes;
    }

    @Override
    public Product resolveProduct(String code) {
        return products.get(code);
    }

    @Override
    public Map<String, Product> resolveProducts(Collection<String> productCodes) {
        Map<String, Product> result = new HashMap<>();

        for (String code : productCodes) {
            result.put(code, products.get(code));
        }

        return result;
    }

    @Override
    public void createProduct(Product product) {
        products.put(product.getCode(), product);
    }

    @Override
    public void updateProduct(Product product) {
        products.put(product.getCode(), product);
    }

    @Override
    public void deleteProduct(String productCode) {
        products.remove(productCode);
    }
}
