package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.content.ContentPageManager;
import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.content.elements.TextContentElement;

public class ContentElementSelector {

    public ContentElement getElement(ContentPage contentPage, String path){
        ContentElement element = ContentPageManager.resolve(contentPage, path);
        if(element == null){
            throw new IllegalArgumentException("No content element found for path '" + path + "'!");
        }

        return element;
    }

    public String getName(ContentPage contentPage, String path){
        ContentElement element = getElement(contentPage, path);
        return element.getName();
    }

    public String getTitle(ContentPage contentPage, String path){
        ContentElement element = getElement(contentPage, path);
        return element.getTitle();
    }

    public TextContentElement getTextElement(ContentPage contentPage, String path){
        ContentElement element = getElement(contentPage, path);

        if(!TextContentElement.class.isAssignableFrom(element.getClass())){
            throw new IllegalArgumentException("Content element on path '" + path + "' is not a text element!");
        }

        return TextContentElement.class.cast(element);
    }

    public String getText(ContentPage contentPage, String path){
        TextContentElement textElement = getTextElement(contentPage, path);
        return textElement.getContent();
    }
}
