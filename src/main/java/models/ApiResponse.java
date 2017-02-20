package models;

import java.util.Locale;

public class ApiResponse {
    private String base;
    private String date;
    private Rate rates;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Rate getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{ base: %s, date: %s, rates: %s }", base, date, rates);
    }
}
