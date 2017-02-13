package com.fourigin.cms.template.engine.utilities;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.ContentPageManager;
import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.content.elements.ContentElementsContainer;
import com.fourigin.cms.models.content.elements.ContentGroup;
import com.fourigin.cms.models.content.elements.ContentList;
import com.fourigin.cms.models.content.elements.LinkAwareContentElement;
import com.fourigin.cms.models.content.elements.ObjectAwareContentElement;
import com.fourigin.cms.models.content.elements.TextAwareContentElement;
import com.fourigin.cms.models.content.elements.list.ContentListElement;
import com.fourigin.cms.template.engine.IncompatibleContentElementException;

import java.util.List;

public class ContentElementUtility implements ContentPageAwareThymeleafTemplateUtility {

    private ContentPage contentPage;

    public ContentElement getElement(String path){
        ContentElement element = ContentPageManager.resolve(contentPage, path);
        if(element == null){
            throw new IllegalArgumentException("No content element found for path '" + path + "'!");
        }

        return element;
    }

    public ContentElement getElement(ContentElementsContainer container, String path){
        ContentElement element = ContentPageManager.resolve(container, path);
        if(element == null){
            throw new IllegalArgumentException("No content element found for path '" + path + "'!");
        }

        return element;
    }

    public String getName(String path){
        ContentElement element = getElement(path);
        return element.getName();
    }

    public String getName(ContentElementsContainer container, String path){
        ContentElement element = getElement(container, path);
        return element.getName();
    }

    public String getTitle(String path){
        ContentElement element = getElement(path);
        return element.getTitle();
    }

    public String getTitle(ContentElementsContainer container, String path){
        ContentElement element = getElement(container, path);
        return element.getTitle();
    }

    public String getText(String path){
        TextAwareContentElement textElement = getTextAwareElement(path);
        return textElement.getContent();
    }

    public String getText(ContentElementsContainer container, String path){
        TextAwareContentElement textElement = getTextAwareElement(container, path);
        return textElement.getContent();
    }

    public List<ContentListElement> listElements(String path){
        ContentList list = getContentListElement(path);
        return list.getElements();
    }

    private TextAwareContentElement getTextAwareElement(String path){
        ContentElement element = getElement(path);

        if(!TextAwareContentElement.class.isAssignableFrom(element.getClass())){
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a text-aware element!");
        }

        return TextAwareContentElement.class.cast(element);
    }

    private TextAwareContentElement getTextAwareElement(ContentElementsContainer container, String path){
        ContentElement element = getElement(container, path);

        if(!TextAwareContentElement.class.isAssignableFrom(element.getClass())){
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a text-aware element!");
        }

        return TextAwareContentElement.class.cast(element);
    }

    private LinkAwareContentElement getLinkAwareElement(String path){
        ContentElement element = getElement(path);

        if(!LinkAwareContentElement.class.isAssignableFrom(element.getClass())){
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a link-aware element!");
        }

        return LinkAwareContentElement.class.cast(element);
    }

    private ObjectAwareContentElement getObjectAwareElement(String path){
        ContentElement element = getElement(path);

        if(!ObjectAwareContentElement.class.isAssignableFrom(element.getClass())){
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a object-aware element!");
        }

        return ObjectAwareContentElement.class.cast(element);
    }

    private ContentList getContentListElement(String path){
        ContentElement element = getElement(path);

        if(!ContentList.class.isAssignableFrom(element.getClass())){
            throw new IncompatibleContentElementException("Content element on path '" + path + "' is not a content-list element!");
        }

        return ContentList.class.cast(element);
    }

    @Override
    public void setContentPage(ContentPage contentPage) {
        this.contentPage = contentPage;
    }
}
