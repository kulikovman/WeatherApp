package ru.kulikovman.weather.Common;


import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {

    @NonNull
    public static String apiRequest(String lat, String lng) {
        String API_KEY = "7d1965355e589cda69560e801ea4e1ea";
        String API_LINK = "http://api.openweathermap.org/data/2.5/weather";

        StringBuilder sb = new StringBuilder(API_LINK);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", lat, lng, API_KEY));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = new Date();
        date.setTime((long) unixTimeStamp * 1000);
        return dateFormat.format(date);
    }

    public static Date convertedTime(double unixTimeStamp) {
        Date date = new Date();
        date.setTime((long) unixTimeStamp * 1000);
        return date;
    }

    public static Date getTimeNow() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        return date;
    }

    public static String getDateNow() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
