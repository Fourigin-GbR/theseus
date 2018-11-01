package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Objects;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 7718217873765520514L;

    private String previousNameplate;
    private NameplateRegistrationOption newNameplateOption;
    private String newNameplateAdditionalInfo;
    private String vehicleIdentNumber;
    private String vehicleId;
    private boolean leasingOrFinancingOption;
    private String insuranceId;

    public String getPreviousNameplate() {
        return previousNameplate;
    }

    public void setPreviousNameplate(String previousNameplate) {
        this.previousNameplate = previousNameplate;
    }

    public NameplateRegistrationOption getNewNameplateOption() {
        return newNameplateOption;
    }

    public void setNewNameplateOption(NameplateRegistrationOption newNameplateOption) {
        this.newNameplateOption = newNameplateOption;
    }

    public String getNewNameplateAdditionalInfo() {
        return newNameplateAdditionalInfo;
    }

    public void setNewNameplateAdditionalInfo(String newNameplateAdditionalInfo) {
        this.newNameplateAdditionalInfo = newNameplateAdditionalInfo;
    }

    public String getVehicleIdentNumber() {
        return vehicleIdentNumber;
    }

    public void setVehicleIdentNumber(String vehicleIdentNumber) {
        this.vehicleIdentNumber = vehicleIdentNumber;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isLeasingOrFinancingOption() {
        return leasingOrFinancingOption;
    }

    public void setLeasingOrFinancingOption(boolean leasingOrFinancingOption) {
        this.leasingOrFinancingOption = leasingOrFinancingOption;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return leasingOrFinancingOption == vehicle.leasingOrFinancingOption &&
            Objects.equals(previousNameplate, vehicle.previousNameplate) &&
            newNameplateOption == vehicle.newNameplateOption &&
            Objects.equals(newNameplateAdditionalInfo, vehicle.newNameplateAdditionalInfo) &&
            Objects.equals(vehicleIdentNumber, vehicle.vehicleIdentNumber) &&
            Objects.equals(vehicleId, vehicle.vehicleId) &&
            Objects.equals(insuranceId, vehicle.insuranceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previousNameplate, newNameplateOption, newNameplateAdditionalInfo, vehicleIdentNumber, vehicleId, leasingOrFinancingOption, insuranceId);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
            "previousNameplate='" + previousNameplate + '\'' +
            ", newNameplateOption=" + newNameplateOption +
            ", newNameplateAdditionalInfo='" + newNameplateAdditionalInfo + '\'' +
            ", vehicleIdentNumber='" + vehicleIdentNumber + '\'' +
            ", vehicleId='" + vehicleId + '\'' +
            ", leasingOrFinancingOption=" + leasingOrFinancingOption +
            ", insuranceId='" + insuranceId + '\'' +
            '}';
    }
}
