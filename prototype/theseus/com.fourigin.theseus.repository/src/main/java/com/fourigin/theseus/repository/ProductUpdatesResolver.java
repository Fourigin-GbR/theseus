package com.fourigin.theseus.repository;

import com.fourigin.theseus.core.updates.ProductUpdate;

import java.util.List;

public interface ProductUpdatesResolver {
    List<ProductUpdate> resolveProductUpdates(ProductUpdatesSearchFilter filter);
}
