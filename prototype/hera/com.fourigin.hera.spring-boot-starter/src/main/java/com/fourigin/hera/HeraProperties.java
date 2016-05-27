package com.fourigin.hera;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = HeraProperties.PREFIX)
public class HeraProperties {
    public static final String PREFIX = "hera";

    private static final String DEFAULT_APPLICATION_NAME = "hera";
    private static final String DEFAULT_CONTEXT_PATH = "/hera";

    private String applicationName = DEFAULT_APPLICATION_NAME;
    private String contextPath = DEFAULT_CONTEXT_PATH;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeraProperties)) return false;

        HeraProperties that = (HeraProperties) o;

        //noinspection SimplifiableIfStatement
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null)
            return false;
        return contextPath != null ? contextPath.equals(that.contextPath) : that.contextPath == null;

    }

    @Override
    public int hashCode() {
        int result = applicationName != null ? applicationName.hashCode() : 0;
        result = 31 * result + (contextPath != null ? contextPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HeraProperties{" +
          "applicationName='" + applicationName + '\'' +
          ", contextPath='" + contextPath + '\'' +
          '}';
    }

}