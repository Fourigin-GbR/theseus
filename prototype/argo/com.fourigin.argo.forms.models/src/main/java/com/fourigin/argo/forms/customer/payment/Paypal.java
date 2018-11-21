package com.fourigin.argo.forms.customer.payment;

import java.io.Serializable;
import java.util.Objects;

public class Paypal implements PaymentMethod, Serializable {
    private static final long serialVersionUID = -7122619902751378501L;

    private String name;
    private String email;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paypal)) return false;
        Paypal paypal = (Paypal) o;
        return Objects.equals(name, paypal.name) &&
            Objects.equals(email, paypal.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }

    @Override
    public String toString() {
        return "Paypal{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
