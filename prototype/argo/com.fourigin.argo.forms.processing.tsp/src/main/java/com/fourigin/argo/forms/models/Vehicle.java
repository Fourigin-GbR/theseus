package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Objects;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 7718217873765520514L;

    private String previousNameplate;
    private NameplateRegistrationOption newNameplateOption;
    private String newNameplateAdditionalInfo;

    private NameplateTypeOption nameplateTypeOption;
    private int seasonStartMonth;
    private int seasonEndMonth;

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

    public NameplateTypeOption getNameplateTypeOption() {
        return nameplateTypeOption;
    }

    public void setNameplateTypeOption(NameplateTypeOption nameplateTypeOption) {
        this.nameplateTypeOption = nameplateTypeOption;
    }

    public int getSeasonStartMonth() {
        return seasonStartMonth;
    }

    public void setSeasonStartMonth(int seasonStartMonth) {
        this.seasonStartMonth = seasonStartMonth;
    }

    public int getSeasonEndMonth() {
        return seasonEndMonth;
    }

    public void setSeasonEndMonth(int seasonEndMonth) {
        this.seasonEndMonth = seasonEndMonth;
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
        return seasonStartMonth == vehicle.seasonStartMonth &&
            seasonEndMonth == vehicle.seasonEndMonth &&
            leasingOrFinancingOption == vehicle.leasingOrFinancingOption &&
            Objects.equals(previousNameplate, vehicle.previousNameplate) &&
            newNameplateOption == vehicle.newNameplateOption &&
            Objects.equals(newNameplateAdditionalInfo, vehicle.newNameplateAdditionalInfo) &&
            nameplateTypeOption == vehicle.nameplateTypeOption &&
            Objects.equals(vehicleIdentNumber, vehicle.vehicleIdentNumber) &&
            Objects.equals(vehicleId, vehicle.vehicleId) &&
            Objects.equals(insuranceId, vehicle.insuranceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previousNameplate, newNameplateOption, newNameplateAdditionalInfo, nameplateTypeOption, seasonStartMonth, seasonEndMonth, vehicleIdentNumber, vehicleId, leasingOrFinancingOption, insuranceId);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
            "previousNameplate='" + previousNameplate + '\'' +
            ", newNameplateOption=" + newNameplateOption +
            ", newNameplateAdditionalInfo='" + newNameplateAdditionalInfo + '\'' +
            ", nameplateTypeOption=" + nameplateTypeOption +
            ", seasonStartMonth=" + seasonStartMonth +
            ", seasonEndMonth=" + seasonEndMonth +
            ", vehicleIdentNumber='" + vehicleIdentNumber + '\'' +
            ", vehicleId='" + vehicleId + '\'' +
            ", leasingOrFinancingOption=" + leasingOrFinancingOption +
            ", insuranceId='" + insuranceId + '\'' +
            '}';
    }
}
