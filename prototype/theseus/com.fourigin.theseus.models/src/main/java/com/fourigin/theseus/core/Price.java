package com.fourigin.theseus.core;

import com.fourigin.theseus.core.builder.PriceMapBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Price implements Comparable<Price>, Serializable {
    private static final long serialVersionUID = 112944152235618926L;

    private Map<String, Double> values = new HashMap<>();
    private String mainCurrency;

    public Price(String currency){
        Objects.requireNonNull(currency, "Currency must not be null!");

        init(currency, new PriceMapBuilder()
            .put(currency, 0.0d)
            .build()
        );
    }

    public Price(String currency, double value){
        Objects.requireNonNull(currency, "Currency must not be null!");

        init(currency, new PriceMapBuilder()
            .put(currency, value)
            .build()
        );
    }

    public Price(String mainCurrency, Map<String, Double> values){
        Objects.requireNonNull(mainCurrency, "mainCurrency must not be null!");
        Objects.requireNonNull(values, "values must not be null!");

        init(mainCurrency, values);
    }

    private void init(String mainCurrency, Map<String, Double> values){
        this.values.clear();

        this.values.putAll(values);
        this.mainCurrency = mainCurrency;
    }

    public double getPrice(){
        return values.get(mainCurrency);
    }

    public double getPrice(String currency){
        if(!values.containsKey(currency)){
            throw new IllegalArgumentException("No price value available for currency '" + currency + "'!");
        }

        return values.get(currency);
    }

    public void setPrice(double value){
        values.put(mainCurrency, value);
    }

    public void setPrice(String currency, double value){
        Objects.requireNonNull(currency, "Currency must not be null!");
        values.put(currency, value);
    }

    @Override
    public int compareTo(Price p) {
        Double mainValue = values.get(mainCurrency);
        Double otherMainValue = p.values.get(mainCurrency);
        if(mainValue == null)
            return -1;
        if(otherMainValue == null)
            return 1;

        return mainValue.compareTo(otherMainValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;

        Price price = (Price) o;

        return values.equals(price.values) && mainCurrency.equals(price.mainCurrency);
    }

    @Override
    public int hashCode() {
        int result = values.hashCode();
        result = 31 * result + mainCurrency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Price{" +
            "values=" + values +
            ", mainCurrency='" + mainCurrency + '\'' +
            '}';
    }
}
