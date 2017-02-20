package models;

import java.util.Locale;

public class Rate {
    private String currency;
    private double value;

    public Rate(String currency, double value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{ currency: %s, value: %.2f }", currency, value);
    }
}
