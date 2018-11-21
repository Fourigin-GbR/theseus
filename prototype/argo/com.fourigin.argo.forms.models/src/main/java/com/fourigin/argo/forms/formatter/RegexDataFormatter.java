package com.fourigin.argo.forms.formatter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDataFormatter implements DataFormatter {
    private Pattern selectionPatternInstance;

    private String selectionPattern;
    private String replacementPattern;

    public static final String SELECTION_PATTERN = "selection-pattern";
    public static final String REPLACEMENT_PATTERN = "replacement-pattern";

    @Override
    public String format(String data) {
        if (data == null) {
            return null;
        }

        Matcher matcher = selectionPatternInstance.matcher(data);
        return matcher.replaceAll(replacementPattern);
    }

    @Override
    public Collection<String> requiredKeys() {
        return Arrays.asList(SELECTION_PATTERN, REPLACEMENT_PATTERN);
    }

    @Override
    public void initialize(Map<String, Object> settings) {
        Objects.requireNonNull(settings, "settings must not be null!");

        selectionPattern = (String) settings.get(SELECTION_PATTERN);
        replacementPattern = (String) settings.get(REPLACEMENT_PATTERN);

        selectionPatternInstance = Pattern.compile(selectionPattern);
    }

    @Override
    public String toString() {
        return "RegexDataFormatter{" +
            "selectionPattern='" + selectionPattern + '\'' +
            ", replacementPattern='" + replacementPattern + '\'' +
            '}';
    }
}
