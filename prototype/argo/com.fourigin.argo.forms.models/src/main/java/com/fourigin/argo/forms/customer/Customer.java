package com.fourigin.argo.forms.customer;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class Customer implements Serializable {
    private static final long serialVersionUID = -1363579994073607782L;

    private String id;
    private Gender gender;
    private String firstname;
    private String lastname;
    private String birthname;
    private Date birthdate;
    private String cityOfBorn;
    private CustomerAddress mainAddress;
    private Set<CustomerAddress> additionalAddresses;
    private String email;
    private String phone;
    private String fax;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthname() {
        return birthname;
    }

    public void setBirthname(String birthname) {
        this.birthname = birthname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCityOfBorn() {
        return cityOfBorn;
    }

    public void setCityOfBorn(String cityOfBorn) {
        this.cityOfBorn = cityOfBorn;
    }

    public CustomerAddress getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(CustomerAddress mainAddress) {
        this.mainAddress = mainAddress;
    }

    public Set<CustomerAddress> getAdditionalAddresses() {
        return additionalAddresses;
    }

    public void setAdditionalAddresses(Set<CustomerAddress> additionalAddresses) {
        this.additionalAddresses = additionalAddresses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
            gender == customer.gender &&
            Objects.equals(firstname, customer.firstname) &&
            Objects.equals(lastname, customer.lastname) &&
            Objects.equals(birthname, customer.birthname) &&
            Objects.equals(birthdate, customer.birthdate) &&
            Objects.equals(cityOfBorn, customer.cityOfBorn) &&
            Objects.equals(mainAddress, customer.mainAddress) &&
            Objects.equals(additionalAddresses, customer.additionalAddresses) &&
            Objects.equals(email, customer.email) &&
            Objects.equals(phone, customer.phone) &&
            Objects.equals(fax, customer.fax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gender, firstname, lastname, birthname, birthdate, cityOfBorn, mainAddress, additionalAddresses, email, phone, fax);
    }

    @Override
    public String toString() {
        return "Customer{" +
            "id='" + id + '\'' +
            ", gender=" + gender +
            ", firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            ", birthname='" + birthname + '\'' +
            ", birthdate=" + birthdate +
            ", cityOfBorn='" + cityOfBorn + '\'' +
            ", mainAddress=" + mainAddress +
            ", additionalAddresses=" + additionalAddresses +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", fax='" + fax + '\'' +
            '}';
    }
}
