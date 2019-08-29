package com.fourigin.argo.controller;

import java.io.Serializable;
import java.util.Objects;

public class ServiceResponse implements Serializable {
    private static final long serialVersionUID = -3302269923322102550L;

    private ServiceResponseStatus status;

    public ServiceResponse(ServiceResponseStatus status) {
        this.status = status;
    }

    public ServiceResponseStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceResponse response = (ServiceResponse) o;
        return status == response.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "status=" + status +
                '}';
    }
}
