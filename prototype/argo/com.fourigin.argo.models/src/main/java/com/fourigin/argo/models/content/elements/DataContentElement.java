package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

public class DataContentElement extends AbstractContentElement implements DataAwareContentElement, ContentElement {
    private static final long serialVersionUID = 5066464546311137699L;

    private LanguageContent title;
    private String content;
    private DataType dataType = DataType.STRING;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public static class Builder {
        private String name;
        private LanguageContent title = new LanguageContent();
        private Map<String, String> attributes = new HashMap<>();
        private String content;
        private DataType dataType = DataType.STRING;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withAttribute(String key, String value) {
            if (key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            this.dataType = DataType.STRING;
            return this;
        }

        public Builder withContent(String content, DataType type) {
            this.content = content;
            this.dataType = type;
            return this;
        }

        public DataContentElement build() {
            DataContentElement element = new DataContentElement();

            element.setName(name);
            if (!title.isEmpty()) {
                element.setTitle(title);
            }
            if (!attributes.isEmpty()) {
                element.setAttributes(attributes);
            }
            element.setContent(content);
            element.setDataType(dataType);

            return element;
        }
    }
}
