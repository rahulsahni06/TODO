package com.sahni.rahul.todo.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sahni on 30-Jun-17.
 */

public class EpochToDate {

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

    public static long NO_DATE_SELECTED = -1;
    public static long NO_TIME_SELECTED = -1;

    public static String convert(long epochSeconds){


        if(epochSeconds == NO_DATE_SELECTED){
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
        if(epochSeconds == NO_TIME_SELECTED){
            return "";
        }
        Date date = new Date(epochSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return formatter.format(date);

    }
}
