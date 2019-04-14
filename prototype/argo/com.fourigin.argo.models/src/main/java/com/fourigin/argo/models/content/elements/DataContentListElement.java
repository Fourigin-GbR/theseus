package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

public class DataContentListElement extends AbstractAttributesAwareContentElement implements DataAwareContentElement, TitleAwareContentElement, ContentListElement {
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

    public void setContent(String content) {
        this.content = content;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public static class Builder {
        private LanguageContent title = new LanguageContent();
        private Map<String, String> attributes = new HashMap<>();
        private String content;
        private DataType dataType = DataType.STRING;

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

        public Builder withContent(String value) {
            this.content = value;
            this.dataType = DataType.STRING;
            return this;
        }

        public Builder withContent(String value, DataType type) {
            this.content = value;
            this.dataType = type;
            return this;
        }

        public DataContentListElement build() {
            DataContentListElement element = new DataContentListElement();

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
