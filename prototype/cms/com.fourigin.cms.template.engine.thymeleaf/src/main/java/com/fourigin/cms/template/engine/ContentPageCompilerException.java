package com.fourigin.cms.template.engine;

public class ContentPageCompilerException extends RuntimeException {
    private static final long serialVersionUID = -7897698509220714367L;

    public ContentPageCompilerException() {
    }

    public ContentPageCompilerException(String message) {
        super(message);
    }

    public ContentPageCompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentPageCompilerException(Throwable cause) {
        super(cause);
    }
}
