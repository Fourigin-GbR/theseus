package com.fourigin.argo.forms.customer.payment;

import java.io.Serializable;
import java.util.Objects;

public class BankAccount implements PaymentMethod, Serializable {
    private static final long serialVersionUID = 1621683865999931638L;

    private String name;
    private String iban;
    private String bic;
    private String bankName;
    private String accountHolder;

    @Override
    public String getDisplayName() {
        return bankName + " " + iban;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount)) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(iban, that.iban) &&
            Objects.equals(bic, that.bic) &&
            Objects.equals(bankName, that.bankName) &&
            Objects.equals(accountHolder, that.accountHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, iban, bic, bankName, accountHolder);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
            "name='" + name + '\'' +
            ", iban='" + iban + '\'' +
            ", bic='" + bic + '\'' +
            ", bankName='" + bankName + '\'' +
            ", accountHolder='" + accountHolder + '\'' +
            '}';
    }
}
