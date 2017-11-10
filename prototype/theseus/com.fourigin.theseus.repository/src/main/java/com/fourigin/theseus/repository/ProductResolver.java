package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.repository.filter.ProductSearchFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductResolver {
    Map<String, Object> getInfo();
    List<String> findProductCodes(ProductSearchFilter filter);

    Product resolveProduct(String code);
    Map<String, Product> resolveProducts(Collection<String> productCodes);
}
