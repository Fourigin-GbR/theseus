package com.fourigin.argo.forms.customer.payment;

import java.io.Serializable;
import java.util.Objects;

public class Sofort implements PaymentMethod, Serializable {
    private static final long serialVersionUID = -3063866766443155065L;

    private String name;

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sofort)) return false;
        Sofort sofort = (Sofort) o;
        return Objects.equals(name, sofort.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Sofort{" +
            "name='" + name + '\'' +
            '}';
    }
}
