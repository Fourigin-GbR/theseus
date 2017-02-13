package com.fourigin.cms.models.content.elements;

abstract public class AbstractContentElement implements ContentElement {
    private static final long serialVersionUID = 2481851091542511335L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContentElement)) return false;

        AbstractContentElement that = (AbstractContentElement) o;

        //noinspection SimplifiableIfStatement
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return title != null ? title.equals(that.title) : that.title == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

}
