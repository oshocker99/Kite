package com.team100.kite_master.messages.messages_data_classes;

import java.util.Calendar;
import java.util.Date;

public class Message {

    private String username;
    private String text;
    private String date;

    public Message(String username, String text) {

        this.username = username;
        this.text = text;
        this.date = getCurrentDateAndTime();
    }

    public String getCurrentDateAndTime() {

        // Initialize time variables
        Calendar now;

        int hour_int;
        String hour_string;
        int minute_int;
        String minute_string;

        int meridiem_int;
        String meridiem_string;

        now = Calendar.getInstance();

        // Get the time information
        hour_int = now.get(Calendar.HOUR);
        minute_int = now.get(Calendar.MINUTE);
        meridiem_int = now.get(Calendar.AM_PM);

        // Set the string for hour
        if (hour_int == 0) {

            hour_string = 12 + "";
        }
        else {

            hour_string = hour_int + "";
        }

        // Set the string for minutes
        if (minute_int < 10) {

            minute_string = "0" + minute_int;
        }
        else {

            minute_string = minute_int + "";
        }

        // Set the string for meridiem
        if (meridiem_int == 0) {

            meridiem_string = "AM";
        }
        else {

            meridiem_string = "PM";
        }

        return hour_string + ":" + minute_string + " " + meridiem_string;
    }

    // Getter and setter methods
    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getText() {

        return this.text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public String getMessageTime() {

        return this.date;
    }
}
