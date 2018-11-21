package com.fourigin.utilities.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExStringFormatter implements StringFormatter {
    private Pattern selectionPatternInstance;

    private String selectionPattern;
    private String replacingPattern;

    private final Logger logger = LoggerFactory.getLogger(RegExStringFormatter.class);

    @Override
    public String format(String data) {
        if (logger.isDebugEnabled()) logger.debug("Formatting {}", data);

        Matcher matcher = selectionPatternInstance.matcher(data);
        return matcher.replaceAll(replacingPattern);
    }

    public void setSelectionPattern(String selectionPattern) {
        Objects.requireNonNull(selectionPattern, "selectionPattern must not be null!");

        if (logger.isInfoEnabled()) logger.info("Using {} as a selectionPattern pattern", selectionPattern);

        this.selectionPattern = selectionPattern;
        this.selectionPatternInstance = Pattern.compile(selectionPattern);
    }

    public void setReplacingPattern(String replacingPattern) {
        Objects.requireNonNull(replacingPattern, "replacingPattern must not be null!");

        if (logger.isInfoEnabled()) logger.info("Using {} as a replacingPattern pattern", replacingPattern);

        this.replacingPattern = replacingPattern;
    }

    @Override
    public String toString() {
        return "RegExStringFormatter{" +
            "selectionPattern='" + selectionPattern + '\'' +
            ", replacingPattern='" + replacingPattern + '\'' +
            '}';
    }
}
