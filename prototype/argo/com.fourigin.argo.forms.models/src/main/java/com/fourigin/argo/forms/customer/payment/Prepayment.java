package com.fourigin.argo.forms.customer.payment;

import java.io.Serializable;
import java.util.Objects;

public class Prepayment implements PaymentMethod, Serializable {
    private static final long serialVersionUID = 6490402125182568558L;

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
        if (!(o instanceof Prepayment)) return false;
        Prepayment that = (Prepayment) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Prepayment{" +
            "name='" + name + '\'' +
            '}';
    }
}
