package com.fourigin.cms.models.content.elements;

public interface LinkAwareContentElement {

    String getUrl();
    void setUrl(String url);

    String getAnchorName();
    void setAnchorName(String anchorName);

    String getTarget();
    void setTarget(String target);

}
