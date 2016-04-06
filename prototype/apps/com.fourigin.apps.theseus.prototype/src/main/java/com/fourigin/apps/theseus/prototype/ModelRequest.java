package com.fourigin.apps.theseus.prototype;

public class ModelRequest {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelRequest)) return false;

        ModelRequest that = (ModelRequest) o;

        return code != null ? code.equals(that.code) : that.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ModelRequest{" +
          "id='" + code + '\'' +
          '}';
    }
}