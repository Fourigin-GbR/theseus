package com.fourigin.hera;

public class LoggerConfiguration {
    private String applicationName;
    private String loggerName;
    private boolean traceEnabled;
    private boolean debugEnabled;
    private boolean infoEnabled;
    private boolean warnEnabled;
    private boolean errorEnabled;

    private boolean consoleLoggingEnabled;
    private boolean serverLoggingEnabled;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isInfoEnabled() {
        return infoEnabled;
    }

    public void setInfoEnabled(boolean infoEnabled) {
        this.infoEnabled = infoEnabled;
    }

    public boolean isWarnEnabled() {
        return warnEnabled;
    }

    public void setWarnEnabled(boolean warnEnabled) {
        this.warnEnabled = warnEnabled;
    }

    public boolean isErrorEnabled() {
        return errorEnabled;
    }

    public void setErrorEnabled(boolean errorEnabled) {
        this.errorEnabled = errorEnabled;
    }

    public boolean isConsoleLoggingEnabled() {
        return consoleLoggingEnabled;
    }

    public void setConsoleLoggingEnabled(boolean consoleLoggingEnabled) {
        this.consoleLoggingEnabled = consoleLoggingEnabled;
    }

    public boolean isServerLoggingEnabled() {
        return serverLoggingEnabled;
    }

    public void setServerLoggingEnabled(boolean serverLoggingEnabled) {
        this.serverLoggingEnabled = serverLoggingEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggerConfiguration)) return false;

        LoggerConfiguration that = (LoggerConfiguration) o;

        if (traceEnabled != that.traceEnabled) return false;
        if (debugEnabled != that.debugEnabled) return false;
        if (infoEnabled != that.infoEnabled) return false;
        if (warnEnabled != that.warnEnabled) return false;
        if (errorEnabled != that.errorEnabled) return false;
        if (consoleLoggingEnabled != that.consoleLoggingEnabled) return false;
        if (serverLoggingEnabled != that.serverLoggingEnabled) return false;
        //noinspection SimplifiableIfStatement
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null)
            return false;
        return loggerName != null ? loggerName.equals(that.loggerName) : that.loggerName == null;

    }

    @Override
    public int hashCode() {
        int result = applicationName != null ? applicationName.hashCode() : 0;
        result = 31 * result + (loggerName != null ? loggerName.hashCode() : 0);
        result = 31 * result + (traceEnabled ? 1 : 0);
        result = 31 * result + (debugEnabled ? 1 : 0);
        result = 31 * result + (infoEnabled ? 1 : 0);
        result = 31 * result + (warnEnabled ? 1 : 0);
        result = 31 * result + (errorEnabled ? 1 : 0);
        result = 31 * result + (consoleLoggingEnabled ? 1 : 0);
        result = 31 * result + (serverLoggingEnabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoggerConfiguration{" +
          "applicationName='" + applicationName + '\'' +
          ", loggerName='" + loggerName + '\'' +
          ", traceEnabled=" + traceEnabled +
          ", debugEnabled=" + debugEnabled +
          ", infoEnabled=" + infoEnabled +
          ", warnEnabled=" + warnEnabled +
          ", errorEnabled=" + errorEnabled +
          ", consoleLoggingEnabled=" + consoleLoggingEnabled +
          ", serverLoggingEnabled=" + serverLoggingEnabled +
          '}';
    }


}