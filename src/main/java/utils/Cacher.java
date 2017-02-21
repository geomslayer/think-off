package utils;

import models.ApiResponse;
import models.Rate;

import java.io.*;

public class Cacher {

    private static final File dir;

    static {
        dir = new File("cache");
    }

    private Cacher() {}

    public static void save(ApiResponse data) {
        File entry = new File(dir, formFilename(data.getBase(), data.getRates().getCurrency()));
        dir.mkdir();
        try {
            entry.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(entry))) {
            writer.println(data.getDate());
            writer.println(data.getRates().getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ApiResponse restore(String from, String to) {
        File entry = new File(dir, formFilename(from, to));
        ApiResponse res = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(entry))) {
            String cachedDate = reader.readLine();
            String value = reader.readLine();

            if (TimeUtil.updated(cachedDate)) {
                res = new ApiResponse(from, cachedDate, new Rate(to, Double.valueOf(value)));
            }
        } catch (IOException e) {
            // it's OK: there wasn't such query
        }

        return res;
    }

    private static String formFilename(String from, String to) {
        return from + "_" + to;
    }

}
