package com.fourigin.cms.models.content.elements;

public class TextLinkContentElement extends AbstractContentElement implements ContentElement {
    private String content;
    private boolean containingMarkup;
    private String url;
    private String anchorName;
    private String target;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
