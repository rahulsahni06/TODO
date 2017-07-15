package com.sahni.rahul.todo.helpers;

import android.util.Log;

import com.sahni.rahul.todo.database.DatabaseConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sahni on 30-Jun-17.
 */

public class EpochToDateTime {

    /**
     *
     * @param epochSeconds seconds since Epoch
     * @return date in format "EEE dd MMM  yyyy" or "Today" or "Tomorrow" or "Yesterday"
     */

//    public static String convert(long epochSeconds){
//        Date date = new Date(epochSeconds);
//        Calendar calendar = Calendar.getInstance();
////        Date currentDate = calendar.getTime();
////        Date date1 = new Date();
////        calendar.add();
////
////        calendar.after()
////        if(date == currentDate){
////
////        }
//        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault());
//        return format.format(date);
//    }

//    public static long NO_DATE_SELECTED = -1;
//    public static long NO_TIME_SELECTED = -1;

    public static String convert(long epochSeconds){


        if(epochSeconds == DatabaseConstants.DATE_NOT_SET){
            return "";
        }

        Date date = new Date(epochSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        Date currDate = new Date();
        String dateToCheck = formatter.format(date);
        String currentDate = formatter.format(currDate);

        if(dateToCheck.equals(currentDate)){
            return "Today";

        }else{
            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date prevDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            Date dateNext = calendar.getTime();

            String previousDate = formatter.format(prevDate);
            String nextDate = formatter.format(dateNext);

            if(previousDate.equals(dateToCheck)){
                return "Yesterday";

            }else if(nextDate.equals(dateToCheck)){
                return "Tomorrow";

            }

            return dateToCheck;

        }


    }

    public static String convertTime(long epochSeconds){
        if(epochSeconds == DatabaseConstants.TIME_NOT_SET){
            return "";
        }
        Date date = new Date(epochSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        return formatter.format(date);

    }

    public static boolean checkTodayDate(long epochSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(epochSeconds);
        Date todayDate = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        String todayDateString = formatter.format(todayDate);
        Log.i("CheckToday"," date = "+dateString);
        Log.i("CheckToday"," current date = "+todayDateString);
        return todayDateString.equals(dateString);
    }
}
