package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.Nullable;
import models.ApiResponse;
import models.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Fetcher {

    private static final String baseUrl = "http://api.fixer.io/latest?base=%s&symbols=%s";

    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Rate.class, new RatesDeserializer())
                    .create();
        }
        return gson;
    }

    @Nullable
    public static Double fetchCurrency(String from, String to) {
        BufferedReader reader = null;
        Double res = null;

        if (!validate(from) || !validate(to)) {
            return null;
        }

        res = Cacher.restore(from, to);
        if (res != null) {
            System.out.println("Restored cached value");
            return res;
        }

        try {
            URL url = new URL(String.format(baseUrl, from, to));
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ApiResponse response = getGson().fromJson(reader, ApiResponse.class);
            Cacher.save(response);
            res = response.getRates().getValue();
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        } catch (NullPointerException e) {
            if (from.equals(to)) {
                res = 1.0;
            }
            System.err.println(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        return res;
    }

    private static boolean validate(String currency) {
        boolean res = currency.length() == 3;
        for (int i = 0; i < currency.length(); ++i) {
            res &= Character.isLetter(currency.charAt(i));
            res &= Character.isUpperCase(currency.charAt(i));
        }
        return res;
    }
}
