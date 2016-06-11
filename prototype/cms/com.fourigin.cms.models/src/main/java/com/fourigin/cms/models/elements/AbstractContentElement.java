package com.fourigin.cms.models.elements;

abstract public class AbstractContentElement implements ContentElement {
    private String name;
    private String title;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
