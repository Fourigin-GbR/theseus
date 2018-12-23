package com.fourigin.utilities;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class UploadResponse implements Serializable {
    private static final long serialVersionUID = -6081563516514105371L;

    private Map<String, String> successful;
    private Map<String, Throwable> failed;

    public Map<String, String> getSuccessful() {
        return successful;
    }

    public void setSuccessful(Map<String, String> successful) {
        this.successful = successful;
    }

    public Map<String, Throwable> getFailed() {
        return failed;
    }

    public void setFailed(Map<String, Throwable> failed) {
        this.failed = failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadResponse)) return false;
        UploadResponse that = (UploadResponse) o;
        return Objects.equals(successful, that.successful) &&
            Objects.equals(failed, that.failed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, failed);
    }

    @Override
    public String toString() {
        return "UploadAssetsResponse{" +
            "successful=" + successful +
            ", failed=" + failed +
            '}';
    }
}
