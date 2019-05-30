package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Index;

public interface AssetIndexRepository {
    Index getIndex();
    void updateIndex(Index index);
}
