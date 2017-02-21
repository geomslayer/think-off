package com.geomslayer.utils;

import com.geomslayer.models.ApiResponse;
import com.geomslayer.models.Rate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

public class Fetcher {

    private static final String INVALID_INPUT = "Invalid input! Enter currency like USD or RUB.";

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

    // fetches data
    // first it checks data in cache, then tries to load from the network
    public static ApiResponse fetchCurrency(String from, String to) {
        String message = null;
        if (!validate(from) || !validate(to)) {
            return new ApiResponse(INVALID_INPUT);
        }

        ApiResponse res = Cacher.restore(from, to);

        // not stored in cache or the information is old
        if (res == null || !TimeUtil.updated(res.getDate())) {
            URLConnection connection = null;
            try {
                URL url = new URL(String.format(baseUrl, from, to));
                connection = url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
            } catch (IOException e) {
                message = "Magic happens very rarely but it's that case!";
            }

            if (connection != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    res = getGson().fromJson(reader, ApiResponse.class);
                } catch (UnknownHostException e) {
                    message = "No internet connection!";
                } catch (SocketTimeoutException e) {
                    message = "Server doesn't response";
                } catch (IOException e) {
                    message = INVALID_INPUT;
                } catch (Exception e) {
                    message = "Unexpected error.";
                }
            }
        }

        if (res != null) {
            if (res.getRates() == null) {
                if (from.equals(to)) {
                    res.setRates(new Rate(to, 1.0));
                } else {
                    res.setMessage(INVALID_INPUT);
                    return res;
                }
            }
            Cacher.save(res);
            res.setMessage(message);
            res.setDate(TimeUtil.formatDate(res.getDate()));
        } else {
            res = new ApiResponse(message);
        }

        return res;
    }

    // simple validation of currency
    private static boolean validate(String currency) {
        boolean res = currency.length() == 3;
        for (int i = 0; i < currency.length(); ++i) {
            res &= Character.isLetter(currency.charAt(i));
            res &= Character.isUpperCase(currency.charAt(i));
        }
        return res;
    }

    // class for async call
    public static class Loader implements Callable<ApiResponse> {

        private String from;
        private String to;

        public Loader(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public ApiResponse call() throws Exception {
            return fetchCurrency(from, to);
        }
    }

}
