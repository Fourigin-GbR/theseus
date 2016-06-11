package com.fourigin.cms.models;

import java.util.Map;

public interface ContentDataSource {
    String getType();

    Map<String, Object> getContext();
}
