package com.fourigin.cms.models.content.elements;

public class TextContentElement extends AbstractContentElement implements ContentElement {
    private String content;
    private boolean containingMarkup;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isContainingMarkup() {
        return containingMarkup;
    }

    public void setContainingMarkup(boolean containingMarkup) {
        this.containingMarkup = containingMarkup;
    }
}
