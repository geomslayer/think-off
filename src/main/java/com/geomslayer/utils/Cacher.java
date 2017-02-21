package com.geomslayer.utils;

import com.geomslayer.models.ApiResponse;
import com.geomslayer.models.Rate;

import java.io.*;

public class Cacher {

    private static final File dir;

    static {
        dir = new File("cache");
    }

    private Cacher() {}

    public static void save(ApiResponse data) {
        File entry = new File(dir, formFilename(data.getBase(), data.getRates().getCurrency()));
        try {
            dir.mkdir();
        } catch (SecurityException e) {
            // bad, couldn't save cache
            // but we won't ask user to permit us
        }

        try {
            entry.createNewFile();
        } catch (IOException | SecurityException e) {
            // similarly
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(entry))) {
            writer.println(data.getDate());
            writer.println(data.getRates().getValue());
        } catch (IOException | SecurityException e) {
            // similarly
        }

    }

    public static ApiResponse restore(String from, String to) {
        File entry = new File(dir, formFilename(from, to));
        ApiResponse res = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(entry))) {
            String cachedDate = reader.readLine();
            String value = reader.readLine();

            res = new ApiResponse(from, cachedDate, new Rate(to, Double.valueOf(value)));
        } catch (IOException e) {
            // it's OK: it means there wasn't such query yet
        }

        return res;
    }

    private static String formFilename(String from, String to) {
        return from + "_" + to;
    }

}
