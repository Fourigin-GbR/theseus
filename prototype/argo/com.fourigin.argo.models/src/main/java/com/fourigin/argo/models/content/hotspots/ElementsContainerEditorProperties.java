package com.fourigin.argo.models.content.hotspots;

import java.util.Map;

public interface ElementsContainerEditorProperties extends ElementEditorProperties {
    Map<String, ElementEditorProperties> getElements();
}
