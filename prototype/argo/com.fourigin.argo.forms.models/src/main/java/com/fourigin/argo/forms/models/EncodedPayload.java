package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class EncodedPayload implements Serializable {
    private static final long serialVersionUID = 5883640418843993409L;

    private String encodedData;

    public byte[] getEncodedDataAsBytes(){
        return Base64.getDecoder().decode(encodedData);
    }

    public void setEncodedDataFromBytes(byte[] bytes){
        encodedData = new String(Base64.getEncoder().encode(bytes), StandardCharsets.ISO_8859_1);
    }

    public String getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(String encodedData) {
        this.encodedData = encodedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncodedPayload)) return false;
        EncodedPayload that = (EncodedPayload) o;
        return Objects.equals(encodedData, that.encodedData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encodedData);
    }

    @Override
    public String toString() {
        return "PdfPayload{" +
            "encodedData='" + encodedData + '\'' +
            '}';
    }
}
