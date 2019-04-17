package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.ContentPageMetaData;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentElementsContainer;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.ContentList;
import com.fourigin.argo.models.content.elements.ContentListElement;
import com.fourigin.argo.models.content.elements.DataAwareContentElement;
import com.fourigin.argo.models.content.elements.GroupContentListElement;
import com.fourigin.argo.models.content.elements.LanguageContent;
import com.fourigin.argo.models.content.elements.LinkAwareContentElement;
import com.fourigin.argo.models.content.elements.ObjectAwareContentElement;
import com.fourigin.argo.models.content.elements.TextAwareContentElement;
import com.fourigin.argo.models.content.elements.TitleAwareContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.template.engine.IncompatibleContentElementException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContentElementUtility implements ContentPageAwareThymeleafTemplateUtility {

    private ContentPage contentPage;

    private String language;

    public ContentElementType getElementType(ContentElement element) {
        if (TextAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.TEXT;
        }
        if (ObjectAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.OBJECT;
        }
        if (LinkAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.LINK;
        }
        if (ContentGroup.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.GROUP;
        }
        if (ContentList.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.LIST;
        }

        throw new UnsupportedOperationException("Unknown content element type '" + element.getClass() + "'!");
    }

    public ContentElementType getElementType(ContentListElement element) {
        if (TextAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.TEXT;
        }
        if (ObjectAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.OBJECT;
        }
        if (LinkAwareContentElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.LINK;
        }
        if (GroupContentListElement.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.GROUP;
        }
        if (ContentList.class.isAssignableFrom(element.getClass())) {
            return ContentElementType.LIST;
        }

        throw new UnsupportedOperationException("Unknown content element type '" + element.getClass() + "'!");
    }

    public Map<String, Object> getConfig(String name) {
        Map<String, Object> config = ContentPageManager.resolveConfig(contentPage, name);
        if (config == null) {
            throw new IllegalArgumentException("No config found for name '" + name + "'!");
        }

        return config;
    }

    public ContentElement getElement(String path) {
        ContentElement element = ContentPageManager.resolve(contentPage, path);
        if (element == null) {
            throw new IllegalArgumentException("No content element found for path '" + path + "'!");
        }

        return element;
    }

    public ContentElement getElement(ContentElementsContainer container, String path) {
        ContentElement element = ContentPageManager.resolve(container, path);
        if (element == null) {
            throw new IllegalArgumentException("No content element found for path '" + path + "'!");
        }

        return element;
    }

    public DataSourceIdentifier getDataSourceIdentifier(String dataSourceName) {
        Collection<DataSourceContent> dataSources = contentPage.getDataSourceContents();
        if (dataSources == null || dataSources.isEmpty()) {
            throw new IllegalArgumentException("No data-source found for name '" + dataSourceName + "'! No data-sources defined at all!");
        }

        for (DataSourceContent dataSource : dataSources) {
            if (dataSourceName.equals(dataSource.getName())) {
                return dataSource.getIdentifier();
            }
        }

        throw new IllegalArgumentException("No data-source found for name '" + dataSourceName + "'!");
    }

    public String getPageTitle() {
        ContentPageMetaData metaData = contentPage.getMetaData();
        return metaData.getContextSpecificTitle(language, true);
    }

    public String getName(String path) {
        ContentElement element = getElement(path);
        return element.getName();
    }

    public String getName(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);
        return element.getName();
    }

    public String getTitle(String path) {
        TitleAwareContentElement element = getTitleAwareElement(path);
        LanguageContent title = element.getTitle();
        if (title != null) {
            return title.get(language);
        }

        return null;
    }

    public String getTitle(ContentElementsContainer container, String path) {
        TitleAwareContentElement element = getTitleAwareElement(container, path);
        LanguageContent title = element.getTitle();
        if (title != null) {
            return title.get(language);
        }

        return null;
    }

    public String getContent(String path) {
        ContentElement element = getElement(path);
        Class<? extends ContentElement> elementClass = element.getClass();

        if (TextAwareContentElement.class.isAssignableFrom(elementClass)) {
            LanguageContent content = ((TextAwareContentElement) element).getContent();
            if (content != null) {
                return content.get(language);
            }

            return null;
        }

        if (DataAwareContentElement.class.isAssignableFrom(elementClass)) {
            return ((DataAwareContentElement) element).getContent();
        }

        return null;
    }

    public int getContentAsInt(String path) {
        return Integer.parseInt(getContent(path));
    }

    public float getContentAsFloat(String path) {
        return Float.parseFloat(getContent(path));
    }

    public String getContent(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);
        Class<? extends ContentElement> elementClass = element.getClass();

        if (TextAwareContentElement.class.isAssignableFrom(elementClass)) {
            LanguageContent content = ((TextAwareContentElement) element).getContent();
            if (content != null) {
                return content.get(language);
            }

            return null;
        }

        if (DataAwareContentElement.class.isAssignableFrom(elementClass)) {
            return ((DataAwareContentElement) element).getContent();
        }

        return null;
    }

    public int getContentAsInt(ContentElementsContainer container, String path) {
        return Integer.parseInt(getContent(container, path));
    }

    public float getContentAsFloat(ContentElementsContainer container, String path) {
        return Float.parseFloat(getContent(container, path));
    }

    public String getOptionalContent(String path, String defaultValue) {
        String content = getContent(path);
        return content == null ? defaultValue : content;
    }

    public int getOptionalContentAsInt(String path, int defaultValue) {
        return Integer.parseInt(getOptionalContent(path, String.valueOf(defaultValue)));
    }

    public float getOptionalContentAsFloat(String path, float defaultValue) {
        return Float.parseFloat(getOptionalContent(path, String.valueOf(defaultValue)));
    }

    public String getOptionalContent(ContentElementsContainer container, String path, String defaultValue) {
        String content = getContent(container, path);
        return content == null ? defaultValue : content;
    }

    public int getOptionalContentAsInt(ContentElementsContainer container, String path, int defaultValue) {
        return Integer.parseInt(getOptionalContent(container, path, String.valueOf(defaultValue)));
    }

    public float getOptionalContentAsFloat(ContentElementsContainer container, String path, float defaultValue) {
        return Float.parseFloat(getOptionalContent(container, path, String.valueOf(defaultValue)));
    }

    public List<ContentListElement> listElements(String path) {
        ContentList list = getContentListElement(path);
        return list.getElements();
    }

    public List<ContentListElement> listElements(ContentElementsContainer container, String path) {
        ContentList list = getContentListElement(container, path);
        return list.getElements();
    }

    // *** private methods ***

    private TitleAwareContentElement getTitleAwareElement(String path) {
        ContentElement element = getElement(path);

        if (!TitleAwareContentElement.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a title-aware element!");
        }

        return TitleAwareContentElement.class.cast(element);
    }

    private TitleAwareContentElement getTitleAwareElement(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);

        if (!TitleAwareContentElement.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a title-aware element!");
        }

        return TitleAwareContentElement.class.cast(element);
    }

    private ContentList getContentListElement(String path) {
        ContentElement element = getElement(path);

        if (!ContentList.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a content-list element!");
        }

        return ContentList.class.cast(element);
    }

    private ContentList getContentListElement(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);

        if (!ContentList.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a content-list element!");
        }

        return ContentList.class.cast(element);
    }

    // *** getters / setters ***

    @Override
    public void setContentPage(ContentPage contentPage) {
        this.contentPage = contentPage;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language.toLowerCase(Locale.US);
    }
}
