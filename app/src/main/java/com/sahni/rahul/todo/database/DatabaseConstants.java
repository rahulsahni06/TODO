package com.sahni.rahul.todo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by sahni on 15-Jul-17.
 */

public class DatabaseConstants {

    public static int TODO_NOT_DONE = 0;
    public static int TODO_DONE = 1;
    public static int ALARM_NOT_SET = 0;
    public static int ALARM_SET = 1;
    public static int ALARM_OVER = 2;
    public static int TIME_NOT_SET = 0;
    public static int DATE_NOT_SET = 0;

    public static ArrayList<String> getSpinnerArrayList(Context context){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("All");
        arrayList.add("Today");
        arrayList.add("Overdue");
        arrayList.add("General");
        arrayList.add("Personal");
        arrayList.add("Home");
        arrayList.add("Work");
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(context).getReadableDatabase();
        String column[] = {""+TodoOpenHelper.TODO_CATEGORY};
        Cursor cursor = database.query(true, TodoOpenHelper.TODO_TABLE, column,
                TodoOpenHelper.TODO_CATEGORY+" NOT IN ('General','Personal','Home','Work')",null,null, null, null, null);
        while (cursor.moveToNext()){
            String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
            arrayList.add(category);
        }
        arrayList.add("Finished");
        cursor.close();

        return arrayList;


    }



}
