package com.fourigin.cms.repository.model.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.cms.repository.model.JsonDirectoryInfo;
import com.fourigin.cms.repository.model.JsonFileInfo;
import com.fourigin.cms.repository.model.JsonInfo;

public class JsonInfoModule extends SimpleModule {

    private static final long serialVersionUID = 2275998436374596983L;

    public JsonInfoModule() {
        super("JsonInfo", Version.unknownVersion());
        setMixInAnnotation(JsonInfo.class, JsonInfoMixin.class);
        setMixInAnnotation(JsonFileInfo.class, JsonInfoMixin.class);
        setMixInAnnotation(JsonDirectoryInfo.class, JsonInfoMixin.class);
    }
}