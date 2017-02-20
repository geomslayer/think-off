package utils;

import models.ApiResponse;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cacher {

    private static final DateFormat dateFormat;
    private static final File dir;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dir = new File("cache");
    }

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

    public static Double restore(String from, String to) {
        File entry = new File(dir, formFilename(from, to));
        Double res = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(entry))) {
            String cachedDate = reader.readLine();
            String value = reader.readLine();

            Date date = new Date();
            String curDate = dateFormat.format(date);

            if (curDate.equals(cachedDate)) {
                res = Double.valueOf(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    private static String formFilename(String from, String to) {
        return from + "_" + to;
    }

}
