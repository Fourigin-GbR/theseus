package com.fourigin.argo.forms.initialization;

import java.io.Serializable;
import java.util.Objects;

public class InitialValue implements Serializable {
    private static final long serialVersionUID = -7278333168487738784L;

    private String displayName;
    private boolean active;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InitialValue)) return false;
        InitialValue that = (InitialValue) o;
        return active == that.active &&
            Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, active);
    }

    @Override
    public String toString() {
        return "InitialValue{" +
            "displayName='" + displayName + '\'' +
            ", active=" + active +
            '}';
    }
}
