package it.bleb.dpi.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateUtil {

    public static DateUtil instance;

    private DateUtil() {
    }

    /**
     * get array util instance
     *
     * @return
     */
    public static synchronized DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
        }
        return instance;
    }


     public static SimpleDateFormat getDateFormatter(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter;
    }
}
