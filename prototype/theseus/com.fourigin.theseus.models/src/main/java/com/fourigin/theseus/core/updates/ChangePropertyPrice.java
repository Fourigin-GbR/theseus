package com.fourigin.theseus.core.updates;


import com.fourigin.theseus.core.Price;

public class ChangePropertyPrice implements ProductChange {
    private String propertyCode;
    private Price previousPrice;
    private Price newPrice;

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public Price getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(Price previousPrice) {
        this.previousPrice = previousPrice;
    }

    public Price getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Price newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangePropertyPrice)) return false;

        ChangePropertyPrice that = (ChangePropertyPrice) o;

        if (propertyCode != null ? !propertyCode.equals(that.propertyCode) : that.propertyCode != null) return false;
        //noinspection SimplifiableIfStatement
        if (previousPrice != null ? !previousPrice.equals(that.previousPrice) : that.previousPrice != null)
            return false;
        return newPrice != null ? newPrice.equals(that.newPrice) : that.newPrice == null;
    }

    @Override
    public int hashCode() {
        int result = propertyCode != null ? propertyCode.hashCode() : 0;
        result = 31 * result + (previousPrice != null ? previousPrice.hashCode() : 0);
        result = 31 * result + (newPrice != null ? newPrice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangePropertyPrice{" +
            "propertyCode='" + propertyCode + '\'' +
            ", previousPrice=" + previousPrice +
            ", newPrice=" + newPrice +
            '}';
    }
}
