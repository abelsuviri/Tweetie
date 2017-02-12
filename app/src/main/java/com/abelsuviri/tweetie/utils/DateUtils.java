package com.abelsuviri.tweetie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Abel Suviri
 */

public class DateUtils {
    private static String sDatePattern = "dd/MM/yyyy";

    public static String getTweetDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(sDatePattern, Locale.getDefault());
        try {
            String dateString = sdf.format(date);
            String todayString = sdf.format(new Date());
            Date dateFormatted = sdf.parse(dateString);
            Date today = sdf.parse(todayString);

            if (dateFormatted.compareTo(today) == 0) {
                return getTime(date);
            } else {
                return getDate(date);
            }
        } catch (ParseException e) {
            e.getMessage();
            return date.toString();
        }
    }

    public static String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(sDatePattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static String getTime(Date date) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }
}
