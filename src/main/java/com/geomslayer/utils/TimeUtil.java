package com.geomslayer.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    private static final int UPDATE_HOUR = 16;

    private static final TimeZone timeZone;
    private static final DateFormat dateFormat;
    private static final DateFormat dateFormatWithTime;

    static {
        // set timezone of European Central Bank, where we fetch data from
        timeZone = TimeZone.getTimeZone("CET");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(timeZone);
        // here we want to print local time
        dateFormatWithTime = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    }

    private TimeUtil() {}

    public static boolean updated(String cachedDate) {
        Calendar cachedCalendar = parseDate(cachedDate);
        Calendar currentCalendar = Calendar.getInstance(timeZone);
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1);

        // if passed time was less then one day since the moment of loading data
        // then we don't need for update, data isn't updated on server
        return currentCalendar.before(cachedCalendar);
    }

    public static String formatDate(String date) {
        try {
            return dateFormatWithTime.format(parseDate(date).getTime());
        } catch (NullPointerException e) {}
        return null;
    }

    // get exact date when information is updated;
    // on fixer's site was mentioned that it is done every day at 4PM CET;
    // so this function setups this time for given date
    private static Calendar parseDate(String cachedDate) {
        try {
            Date currentDate = dateFormat.parse(cachedDate);
            Calendar cachedCalendar = Calendar.getInstance(timeZone);
            cachedCalendar.setTime(currentDate);
            cachedCalendar.set(Calendar.HOUR, UPDATE_HOUR);
            return cachedCalendar;
        } catch (ParseException e) {}
        return null;
    }

}
