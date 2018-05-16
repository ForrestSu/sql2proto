package com.sunquan.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * @return Integer CurDate YYYYMMDD
     */
    public static int CurDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int output = year * 10000 + month * 100 + day;
        return output;
    }
    /**
     * @return String CurDate YYYYMMDD
     */
    public static String CurDateStr() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String output = String.format("%04d%02d%02d", year, month, day);
        return output;
    }
    /**
     * @return String CurDate YYYY-MM-DD
     */
    public static String CurDateHunman() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String CurTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static int iCurTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return Integer.valueOf(sdf.format(new Date()));
    }
}
