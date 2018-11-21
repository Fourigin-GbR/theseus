package com.fourigin.argo.forms.formatter.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.forms.formatter.DataFormatter;

public class DataFormatterModule extends SimpleModule {

    private static final long serialVersionUID = 3200353333398679937L;

    public DataFormatterModule() {
        super("DataFormatter", Version.unknownVersion());

        setMixInAnnotation(DataFormatter.class, DataFormatterMixin.class);
    }
}
