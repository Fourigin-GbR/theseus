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
import com.fourigin.argo.models.content.elements.GroupContentListElement;
import com.fourigin.argo.models.content.elements.LinkAwareContentElement;
import com.fourigin.argo.models.content.elements.ObjectAwareContentElement;
import com.fourigin.argo.models.content.elements.TextAwareContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.template.engine.IncompatibleContentElementException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContentElementUtility implements ContentPageAwareThymeleafTemplateUtility {

    private ContentPage contentPage;

    private String compilerBase;

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
            if(dataSourceName.equals(dataSource.getName())){
                return dataSource.getIdentifier();
            }
        }

        throw new IllegalArgumentException("No data-source found for name '" + dataSourceName + "'!");
    }

    public String getPageTitle(){
        ContentPageMetaData metaData = contentPage.getMetaData();
        return metaData.getContextSpecificTitle(compilerBase, true);
    }

    public String getName(String path) {
        ContentElement element = getElement(path);
        return element.getName();
    }

    public String getName(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);
        return element.getName();
    }

    // TODO: allow also context specific titles
    public String getTitle(String path) {
        ContentElement element = getElement(path);
        return element.getTitle();
    }

    public String getTitle(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);
        return element.getTitle();
    }

    public String getContent(String path) {
        TextAwareContentElement textElement = getTextAwareElement(path);
        return textElement.getContextSpecificContent(compilerBase, true);
    }

    public int getContentAsInt(String path) {
        return Integer.parseInt(getContent(path));
    }

    public float getContentAsFloat(String path) {
        return Float.parseFloat(getContent(path));
    }

    public String getContent(ContentElementsContainer container, String path) {
        TextAwareContentElement textElement = getTextAwareElement(container, path);
        return textElement.getContextSpecificContent(compilerBase, true);
    }

    public int getContentAsInt(ContentElementsContainer container, String path) {
        return Integer.parseInt(getContent(container, path));
    }

    public float getContentAsFloat(ContentElementsContainer container, String path) {
        return Float.parseFloat(getContent(container, path));
    }

    public String getOptionalContent(String path, String defaultValue) {
        try {
            TextAwareContentElement textElement = getTextAwareElement(path);
            return textElement.getContextSpecificContent(compilerBase, true);
        } catch (Throwable ex) {
            return defaultValue;
        }
    }

    public int getOptionalContentAsInt(String path, int defaultValue) {
        return Integer.parseInt(getOptionalContent(path, String.valueOf(defaultValue)));
    }

    public float getOptionalContentAsFloat(String path, float defaultValue) {
        return Float.parseFloat(getOptionalContent(path, String.valueOf(defaultValue)));
    }

    public String getOptionalContent(ContentElementsContainer container, String path, String fallback) {
        try {
            TextAwareContentElement textElement = getTextAwareElement(container, path);
            return textElement.getContextSpecificContent(compilerBase, true);
        } catch (Throwable ex) {
            return fallback;
        }
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

    private TextAwareContentElement getTextAwareElement(String path) {
        ContentElement element = getElement(path);

        if (!TextAwareContentElement.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a text-aware element!");
        }

        return TextAwareContentElement.class.cast(element);
    }

    private TextAwareContentElement getTextAwareElement(ContentElementsContainer container, String path) {
        ContentElement element = getElement(container, path);

        if (!TextAwareContentElement.class.isAssignableFrom(element.getClass())) {
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a text-aware element!");
        }

        return TextAwareContentElement.class.cast(element);
    }

//    private LinkAwareContentElement getLinkAwareElement(String path){
//        ContentElement element = getElement(path);
//
//        if(!LinkAwareContentElement.class.isAssignableFrom(element.getClass())){
//            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a link-aware element!");
//        }
//
//        return LinkAwareContentElement.class.cast(element);
//    }
//
//    private LinkAwareContentElement getLinkAwareElement(ContentElementsContainer container, String path){
//        ContentElement element = getElement(container, path);
//
//        if(!LinkAwareContentElement.class.isAssignableFrom(element.getClass())){
//            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a link-aware element!");
//        }
//
//        return LinkAwareContentElement.class.cast(element);
//    }
//
//    private ObjectAwareContentElement getObjectAwareElement(String path){
//        ContentElement element = getElement(path);
//
//        if(!ObjectAwareContentElement.class.isAssignableFrom(element.getClass())){
//            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a object-aware element!");
//        }
//
//        return ObjectAwareContentElement.class.cast(element);
//    }
//
//    private ObjectAwareContentElement getObjectAwareElement(ContentElementsContainer container, String path){
//        ContentElement element = getElement(container, path);
//
//        if(!ObjectAwareContentElement.class.isAssignableFrom(element.getClass())){
//            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a object-aware element!");
//        }
//
//        return ObjectAwareContentElement.class.cast(element);
//    }

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
    public void setCompilerBase(String compilerBase) {
        this.compilerBase = compilerBase.toLowerCase(Locale.US);
    }
}
