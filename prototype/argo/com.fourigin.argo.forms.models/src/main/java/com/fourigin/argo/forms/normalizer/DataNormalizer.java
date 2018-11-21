package com.fourigin.argo.forms.normalizer;

import com.fourigin.utilities.reflection.Initializable;

public interface DataNormalizer extends Initializable {
    String normalize(String data);
}
