//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MKDateUtils {
    public static final String FORMART1 = "yyyyMMddHHmmss";
    public static final String FORMART2 = "yyyy-MM-dd HH:mm:ss";

    public MKDateUtils() {
    }

    public static Date stringToDate(String date) throws IllegalArgumentException, ParseException {
        if (date != null && !date.equalsIgnoreCase("")) {
            SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");

            try {
                return formater.parse(date);
            } catch (ParseException var3) {
                throw var3;
            }
        } else {
            throw new IllegalArgumentException("parameter date is not valid");
        }
    }

    public static Date stringToDate(String date, String format) throws IllegalArgumentException, ParseException {
        if (date != null && !date.equalsIgnoreCase("")) {
            SimpleDateFormat formater = new SimpleDateFormat(format);

            try {
                return formater.parse(date);
            } catch (ParseException var4) {
                throw var4;
            }
        } else {
            throw new IllegalArgumentException("parameter date is not valid");
        }
    }

    public static String dateToString(Date date) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("parameter date is not valid");
        } else {
            SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
            return formater.format(date);
        }
    }

    public static String dateToString(Date date, String format) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("parameter date is not valid");
        } else {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            return formater.format(date);
        }
    }

    public static Date addMinute(int minute, Date date) {
        if (date == null) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(12, minute);
            return calendar.getTime();
        }
    }
}
