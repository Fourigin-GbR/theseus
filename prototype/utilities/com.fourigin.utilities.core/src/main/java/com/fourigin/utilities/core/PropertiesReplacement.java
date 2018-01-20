package com.fourigin.utilities.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesReplacement {

    private Pattern pattern;

    private boolean keepUnresolved = true;

    private static final String DEFAULT_REPLACEMENT_PATTERN = "\\$\\{(.+?)\\}";

    public PropertiesReplacement(){
        pattern = Pattern.compile(DEFAULT_REPLACEMENT_PATTERN);
    }

    public PropertiesReplacement(String replacementPattern) {
        pattern = Pattern.compile(replacementPattern);
    }

    public String process(String text, String... properties){
        if(properties == null || properties.length == 0){
            return text;
        }

        if(properties.length % 2 == 1){
            throw new IllegalArgumentException("Invalid number of properties! Required an even number, but found " + properties.length + " !");
        }

        Map<String, String> props = new HashMap<>();
        for(int i=0; i<properties.length / 2; i+=2){
            props.put(properties[i], properties[i+1]);
        }

        return process(text, props);
    }

    public String process(String text, Map<String, String> properties){
        Matcher matcher = pattern.matcher(text);

        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = properties.get(matcher.group(1));

            builder.append(text.substring(i, matcher.start()));
            if (replacement == null) {
                if(keepUnresolved) {
                    builder.append(matcher.group(0));
                }
            }
            else {
                builder.append(replacement);
            }

            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));

        return builder.toString();
    }

    public void setKeepUnresolved(boolean keepUnresolved) {
        this.keepUnresolved = keepUnresolved;
    }
}
