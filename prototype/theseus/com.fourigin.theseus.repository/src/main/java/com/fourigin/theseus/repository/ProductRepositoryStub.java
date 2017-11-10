package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepositoryStub implements ProductRepository {
    private Map<String, Product> products = new HashMap<>();

    private long initializationTimestamp;

    public ProductRepositoryStub() {
        initializationTimestamp = System.currentTimeMillis();
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new HashMap<>();

        result.put("products-update-timestamp", String.valueOf(initializationTimestamp));

        Date initDate = new Date(initializationTimestamp);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//        String formattedInitDate = dateFormat.format(initDate);
        result.put("products-update-date", initDate);

        int count = products != null ? products.size() : 0;
        result.put("products-count", count);

        return result;
    }

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
