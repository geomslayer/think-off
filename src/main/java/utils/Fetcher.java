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

    private Fetcher() {}

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
        if (!validate(from) || !validate(to)) {
            return null;
        }

        ApiResponse res = Cacher.restore(from, to);

        if (res == null) {
            URL url;
            try {
                url = new URL(String.format(baseUrl, from, to));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                res = getGson().fromJson(reader, ApiResponse.class);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        if (res != null) {
            if (res.getRates() == null) {
                if (from.equals(to)) {
                    res.setRates(new Rate(to, 1.0));
                } else {
                    return null;
                }
            }
            Cacher.save(res);
            res.setDate(TimeUtil.formatDate(res.getDate()));
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
