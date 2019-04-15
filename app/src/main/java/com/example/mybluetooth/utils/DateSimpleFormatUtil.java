package com.example.mybluetooth.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateSimpleFormatUtil {
    public static String date2HmsStr(Date date) {
        String dateStr ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        dateStr = dateFormat.format(date);
        return dateStr;
    }

    public static String date2YmdStr(Date date) {
        String dateStr;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        dateStr = dateFormat.format(date);
        return dateStr;
    }
}
