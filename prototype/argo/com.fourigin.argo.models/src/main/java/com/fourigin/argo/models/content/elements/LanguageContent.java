package com.fourigin.argo.models.content.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LanguageContent extends HashMap<String, String> {
    private static final long serialVersionUID = 140113185263030413L;

    private final Logger logger = LoggerFactory.getLogger(LanguageContent.class);

    public LanguageContent(){
        super();

        if (logger.isDebugEnabled()) logger.debug("Initializing an empty LanguageContent");
    }
    
    public LanguageContent(Map<String, String> values){
        super(values);

        if (logger.isDebugEnabled()) logger.debug("Initializing a LanguageContent with values {}", values);
    }

    public static LanguageContent forContent(String ctx, String content) {
        LanguageContent result = new LanguageContent();

        result.put(ctx, content);

        return result;
    }

    public static LanguageContent forContent(String... values) {
        LanguageContent result = new LanguageContent();
        if (values == null || values.length == 0) {
            return result;
        }

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Required odd number of arguments (ctx, value, ...), but found " + values.length + " arguments");
        }

        for (int i = 0; i < values.length; i += 2) {
            result.put(values[i], values[i + 1]);
        }

        return result;
    }
}
