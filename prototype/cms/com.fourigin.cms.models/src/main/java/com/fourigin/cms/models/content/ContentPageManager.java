package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.content.elements.ContentElementsContainer;
import com.fourigin.cms.models.content.elements.ContentGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringTokenizer;

public class ContentPageManager {
    private static final Logger logger = LoggerFactory.getLogger(ContentPageManager.class);

    public static ContentElement resolve(ContentPage contentPage, String contentPath){
        if(contentPage == null){
            if (logger.isInfoEnabled()) logger.info("Unable to resolve ContentElement based on null-ContentPage!");
            return null;
        }
        
        if(contentPath == null){
            if (logger.isInfoEnabled()) logger.info("Unable to resolve ContentElement based on null-path!");
            return null;
        }

        String path = contentPath.replace("//", "/").trim();

        if("/".equals(path)){
            // special case, return all root level elements, packed in a new pseudo group
            ContentGroup group = new ContentGroup();
            group.setName("");
            group.setTitle("");
            group.setElements(contentPage.getContent());
            return group;
        }

        List<ContentElement> elements = contentPage.getContent();
        return resolve(path, elements);
    }

    public static ContentElement resolve(ContentElementsContainer container, String contentPath){
        if(container == null){
            if (logger.isInfoEnabled()) logger.info("Unable to resolve ContentElement based on null-container!");
            return null;
        }

        if(contentPath == null){
            if (logger.isInfoEnabled()) logger.info("Unable to resolve ContentElement based on null-path!");
            return null;
        }

        String path = contentPath.replace("//", "/").trim();

        List<ContentElement> elements = container.getElements();
        return resolve(path, elements);
    }

    private static ContentElement resolve(String path, List<ContentElement> elements){
        ContentElement current = null;

        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();

            current = null;

            for (ContentElement element : elements) {
                if(token.equals(element.getName())){
                    current = element;
                    break;
                }
            }

            if(current == null){
                if (logger.isInfoEnabled()) logger.info("Unable to resolve content path '{}'! No element found for part '{}'!", path, token);
                throw new UnresolvableContentPathException("No element found for part '" + token  + "'!", path);
            }

            if(!ContentGroup.class.isAssignableFrom(current.getClass())){
                if(tok.hasMoreTokens()){
                    if (logger.isInfoEnabled()) logger.info("Unable to resolve content path '{}'! Reached the end of the element hierarchy at '{}'!", path, token);
                    throw new UnresolvableContentPathException("Reached the end of the element hierarchy at '" + token  + "'!", path);
                }

                return current;
            }

            ContentGroup currentGroup = ContentGroup.class.cast(current);
            elements = currentGroup.getElements();
        }

        return current;
    }

    public static void update(ContentPage contentPage, String contentPath, ContentElement element){
        // TODO: implement me!
        throw new UnsupportedOperationException("Implement me!");
    }
}
