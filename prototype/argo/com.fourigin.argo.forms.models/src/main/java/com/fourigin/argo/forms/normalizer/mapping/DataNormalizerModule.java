package com.fourigin.argo.forms.normalizer.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.forms.normalizer.DataNormalizer;

public class DataNormalizerModule extends SimpleModule {

    private static final long serialVersionUID = 1866014089550063088L;

    public DataNormalizerModule() {
        super("DataNormalizer", Version.unknownVersion());

        setMixInAnnotation(DataNormalizer.class, DataNormalizerMixin.class);
    }
}
