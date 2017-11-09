package com.fourigin.theseus.core.updates;

import com.fourigin.theseus.core.Price;

public class ChangeProductPrice implements ProductChange {
    private Price previousPrice;
    private Price newPrice;

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
        if (!(o instanceof ChangeProductPrice)) return false;

        ChangeProductPrice that = (ChangeProductPrice) o;

        //noinspection SimplifiableIfStatement
        if (previousPrice != null ? !previousPrice.equals(that.previousPrice) : that.previousPrice != null)
            return false;
        return newPrice != null ? newPrice.equals(that.newPrice) : that.newPrice == null;
    }

    @Override
    public int hashCode() {
        int result = previousPrice != null ? previousPrice.hashCode() : 0;
        result = 31 * result + (newPrice != null ? newPrice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangeProductPrice{" +
            "previousPrice=" + previousPrice +
            ", newPrice=" + newPrice +
            '}';
    }
}
