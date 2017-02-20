package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.Nullable;
import models.ApiResponse;
import models.Rate;
import utils.RatesDeserializer;

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
    public static ApiResponse fetchCurrency(String from, String to) {
        BufferedReader reader = null;
        ApiResponse res = null;

        if (!validate(from) || !validate(to)) {
            return null;
        }

        try {
            URL url = new URL(String.format(baseUrl, from, to));
            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String json = reader.readLine();
            res = getGson().fromJson(json, ApiResponse.class);
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
