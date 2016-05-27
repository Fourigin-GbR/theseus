package com.fourigin.logger;

import java.util.Arrays;

public class LogRequest {
    private String logger;
    private String message;
    private Object[] args;

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogRequest)) return false;

        LogRequest that = (LogRequest) o;

        if (logger != null ? !logger.equals(that.logger) : that.logger != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, that.args);

    }

    @Override
    public int hashCode() {
        int result = logger != null ? logger.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "LogRequest{" +
          "logger='" + logger + '\'' +
          ", message='" + message + '\'' +
          ", args=" + Arrays.toString(args) +
          '}';
    }


}
