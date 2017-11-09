package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.updates.ProductUpdate;

public interface ProductUpdatesRepository extends ProductUpdatesResolver {
    void createProductUpdate(ProductUpdate update);
    void updateProductUpdate(ProductUpdate update);
    void deleteProductUpdate(ProductUpdate update);
}
