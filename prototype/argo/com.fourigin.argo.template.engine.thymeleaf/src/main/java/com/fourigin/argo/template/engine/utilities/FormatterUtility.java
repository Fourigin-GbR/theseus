package com.fourigin.argo.template.engine.utilities;

import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormatterUtility implements ThymeleafTemplateUtility {

    private String language;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    private PropertiesReplacement replacement = new PropertiesReplacement("\\{(.+?)\\}");

    private final Logger logger = LoggerFactory.getLogger(FormatterUtility.class);

    public int parseInt(String value){
        return Integer.parseInt(value);
    }

    public int parseInt(String value, int fallback){
        try {
            return Integer.parseInt(value);
        }
        catch(NumberFormatException ex){
            return fallback;
        }
    }

    public float parseFloat(String value){
        return Float.parseFloat(value);
    }

    public float parseFloat(String value, float fallback){
        try {
            return Float.parseFloat(value);
        }
        catch(NumberFormatException ex){
            return fallback;
        }
    }
    
    public String formatPrice(String unformattedValue) {
        if (logger.isDebugEnabled()) logger.debug("Using language '{}'", language);

        try {
            double value = Double.parseDouble(unformattedValue);
            return formatter.format(value);
        } catch (NumberFormatException ex) {
            if (logger.isErrorEnabled()) logger.error("Unable to parse price value '" + unformattedValue + "'!", ex);
            return unformattedValue;
        }
    }

    public String formatNumber(String unformattedValue, String unit) {
        if (logger.isDebugEnabled()) logger.debug("Using language '{}'", language);

        try {
            Double.parseDouble(unformattedValue);
            return unformattedValue + " " + unit;
        } catch (NumberFormatException ex) {
            if (logger.isErrorEnabled()) logger.error("Unable to parse number value '" + unformattedValue + "'!", ex);
            return unformattedValue;
        }
    }

    public String formatString(String pattern, String... values) {
        if (values == null || values.length == 0) {
            return pattern;
        }

        Map<String, String> valuesMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            valuesMap.put(String.valueOf(i), values[i]);
        }

        return replacement.process(pattern, valuesMap);
    }

    @Override
    public void setLanguage(String language) {
        this.language = language.toLowerCase(Locale.US);

        this.formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        this.formatter.setMaximumFractionDigits(0);
    }
}
