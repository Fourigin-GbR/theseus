package com.fourigin.argo.forms.customer;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private static final long serialVersionUID = 9208383441929829487L;

    private String street;
    private String houseNumber;
    private String additionalInfo;
    private String zipCode;
    private String city;
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address that = (Address) o;
        return Objects.equals(street, that.street) &&
            Objects.equals(houseNumber, that.houseNumber) &&
            Objects.equals(additionalInfo, that.additionalInfo) &&
            Objects.equals(zipCode, that.zipCode) &&
            Objects.equals(city, that.city) &&
            Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, houseNumber, additionalInfo, zipCode, city, country);
    }

    @Override
    public String toString() {
        return "CustomerAddress{" +
            "street='" + street + '\'' +
            ", houseNumber='" + houseNumber + '\'' +
            ", additionalInfo='" + additionalInfo + '\'' +
            ", zipCode='" + zipCode + '\'' +
            ", city='" + city + '\'' +
            ", country='" + country + '\'' +
            '}';
    }
}
