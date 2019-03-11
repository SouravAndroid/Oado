package com.oado.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Commons {

    public static final String TAG = "oado";

    private Context context;

    public Commons(Context context) {
        this.context = context;
    }


    public static final String dateFormat = "dd/MM/yyyy";
    public static final String timeFormat = "hh:mm a";

    public static String getFirstChar(String s) {
        return s.substring(0, 1);
    }


    public static void restartActivity(Activity act){

        Intent intent=new Intent();
        intent.setClass(act, act.getClass());
        act.startActivity(intent);
        act.finish();

    }

    public static String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

    public static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(timeFormat);
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static boolean isYoutubeLink(String link){
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
        if (!link.isEmpty() && link.matches(pattern)) {
            /// Valid youtube URL
            return true;
        } else{
            // Not Valid youtube URL
            return false;
        }
    }


}
