package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    private static final int UPDATE_HOUR = 16;

    private static final TimeZone timeZone;
    private static final DateFormat dateFormat;
    private static final DateFormat dateFormatWithTime;

    static {
        timeZone = TimeZone.getTimeZone("CET");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(timeZone);
        dateFormatWithTime = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    }

    private TimeUtil() {}

    public static boolean updated(String cachedDate) {
        Calendar cachedCalendar = parseDate(cachedDate);
        Calendar currentCalendar = Calendar.getInstance(timeZone);
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1);

        return currentCalendar.before(cachedCalendar);
    }

    public static String formatDate(String date) {
        try {
            return dateFormatWithTime.format(parseDate(date).getTime());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Calendar parseDate(String cachedDate) {
        try {
            Date currentDate = dateFormat.parse(cachedDate);
            Calendar cachedCalendar = Calendar.getInstance(timeZone);
            cachedCalendar.setTime(currentDate);
            cachedCalendar.set(Calendar.HOUR, UPDATE_HOUR);
            return cachedCalendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
