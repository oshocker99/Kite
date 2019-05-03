package com.team100.kite_master.forum.forum_data_classes;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

    //Copyright 2012 Google Inc.

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = getCurMillis();
        if (time > now || time <= 0) {
            return "just now";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            if (diff / HOUR_MILLIS == 1) {
                return "an hour ago";
            } else {
                return diff / HOUR_MILLIS + " hours ago";
            }
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public long getCurMillis(){
        return System.currentTimeMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public String getCleanDate(long milliSeconds, String dateFormat)
    {
        if (milliSeconds < 1000000000000L) {
            milliSeconds *= 1000;
        }
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        StringBuilder sb = new StringBuilder(formatter.format(cal.getTime()));
        if(sb.charAt(sb.length() - 7) == '0') {
            sb.deleteCharAt(sb.length() - 7);
        }
        if(sb.charAt(0) == '0'){
            sb.deleteCharAt(0);
        }
        String out = sb.toString();
        out = out.replace("AM", "am").replace("PM","pm");
        return out;
    }

}
