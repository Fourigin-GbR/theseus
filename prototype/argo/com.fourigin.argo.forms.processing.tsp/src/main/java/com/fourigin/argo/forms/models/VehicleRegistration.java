package com.fourigin.argo.forms.models;

import com.fourigin.argo.forms.customer.payment.BankAccount;
import com.fourigin.argo.forms.customer.payment.PaymentMethod;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class VehicleRegistration implements Serializable {
    private static final long serialVersionUID = -731726915018271494L;

    private String customerId;
    private Vehicle vehicle;
    private BankAccount bankAccountForTaxPayment;
    private PaymentMethod servicePaymentMethod;
    private HandoverOption handoverOption;

    public static VehicleRegistration fromMap(Map<String, Object> values) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(new VehicleRegistration());
        beanWrapper.setPropertyValues(values);
        return (VehicleRegistration) beanWrapper.getWrappedInstance();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BankAccount getBankAccountForTaxPayment() {
        return bankAccountForTaxPayment;
    }

    public void setBankAccountForTaxPayment(BankAccount bankAccountForTaxPayment) {
        this.bankAccountForTaxPayment = bankAccountForTaxPayment;
    }

    public PaymentMethod getServicePaymentMethod() {
        return servicePaymentMethod;
    }

    public void setServicePaymentMethod(PaymentMethod servicePaymentMethod) {
        this.servicePaymentMethod = servicePaymentMethod;
    }

    public HandoverOption getHandoverOption() {
        return handoverOption;
    }

    public void setHandoverOption(HandoverOption handoverOption) {
        this.handoverOption = handoverOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleRegistration)) return false;
        VehicleRegistration that = (VehicleRegistration) o;
        return Objects.equals(customerId, that.customerId) &&
            Objects.equals(vehicle, that.vehicle) &&
            Objects.equals(bankAccountForTaxPayment, that.bankAccountForTaxPayment) &&
            Objects.equals(servicePaymentMethod, that.servicePaymentMethod) &&
            handoverOption == that.handoverOption;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, vehicle, bankAccountForTaxPayment, servicePaymentMethod, handoverOption);
    }

    @Override
    public String toString() {
        return "VehicleRegistration{" +
            "customerId='" + customerId + '\'' +
            ", vehicle=" + vehicle +
            ", bankAccountForTaxPayment=" + bankAccountForTaxPayment +
            ", servicePaymentMethod=" + servicePaymentMethod +
            ", handoverOption=" + handoverOption +
            '}';
    }
}
