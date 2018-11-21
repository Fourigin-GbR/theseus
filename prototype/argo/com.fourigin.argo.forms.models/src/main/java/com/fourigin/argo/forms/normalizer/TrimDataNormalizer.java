package com.fourigin.argo.forms.normalizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TrimDataNormalizer implements DataNormalizer {
    @Override
    public String normalize(String data) {
        if(data == null){
            return null;
        }

        return data.trim();
    }

    @Override
    public Collection<String> requiredKeys() {
        return Collections.emptySet();
    }

    @Override
    public void initialize(Map<String, Object> settings) {
        //
    }

    @Override
    public String toString() {
        return "TrimDataNormalizer{}";
    }
}
