package com.abelsuviri.tweetie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class has the methods to format the date.
 *
 * @author Abel Suviri
 */

public class DateUtils {
    private static String sDatePattern = "dd/MM/yyyy";

    /**
     * This method checks if the date of a tweet is today or was in the past to format the date as
     * date or as time.
     *
     * @param date This is the date returned by Twitter API.
     * @return Formatted date.
     */
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

    /**
     * It formats the date to a date format dd/MM/yyyy.
     *
     * @param date This is the date returned by Twitter API.
     * @return Formatted date.
     */
    public static String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(sDatePattern, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * It formats the date to a time format HH:mm
     *
     * @param date This is the date returned by Twitter API.
     * @return Formatted date as time.
     */
    public static String getTime(Date date) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }
}
