package com.fourigin.argo.forms.formatter;

import com.fourigin.utilities.reflection.Initializable;

public interface DataFormatter extends Initializable {
    String format(String data);
}
