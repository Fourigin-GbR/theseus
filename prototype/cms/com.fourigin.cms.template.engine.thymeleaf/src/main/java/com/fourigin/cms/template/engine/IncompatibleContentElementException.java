package com.fourigin.cms.template.engine;

public class IncompatibleContentElementException extends RuntimeException {
    private static final long serialVersionUID = 1918314908090962124L;

    public IncompatibleContentElementException() {
    }

    public IncompatibleContentElementException(String message) {
        super(message);
    }
}
