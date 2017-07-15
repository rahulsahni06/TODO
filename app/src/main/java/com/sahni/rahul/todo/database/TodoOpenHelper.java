package com.sahni.rahul.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sahni on 28-Jun-17.
 */

public class TodoOpenHelper extends SQLiteOpenHelper {

    public static final String TODO_ID = "id";
    public static final String TODO_TABLE = "myTodo";
    public static final String TODO_TASK = "task";
    public static final String TODO_CATEGORY = "category";
    public static final String TODO_DATE = "date";
    public static final String TODO_TIME = "time";
    public static final String TODO_STATUS = "status";
    public static final String TODO_PENDING_INTENT_ID = "intent_id";
    public static final String TODO_ALARM_STATUS ="alarm_status";


//    public static int TODO_NOT_DONE = 0;
//    public static int TODO_DONE = 1;
//    public static int ALARM_NOT_SET = 0;
//    public static int ALARM_SET = 1;
//    public static int ALARM_OVER = 2;
//    public static int TIME_NOT_SET = 0;
//    public static int DATE_NOT_SET = 0;




    /**
     * Singleton class, its object is created only once
     */

    static TodoOpenHelper todoOpenHelper;




    public static TodoOpenHelper getOpenHelperInstance(Context context){
        if(todoOpenHelper == null){
            todoOpenHelper = new TodoOpenHelper(context);
        }
        return todoOpenHelper;
    }

    private TodoOpenHelper(Context context) {
        super(context, "TODO.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query  = "CREATE TABLE "+TODO_TABLE +
                " ( "+TODO_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                      TODO_TASK + " TEXT, "+
                      TODO_CATEGORY + " TEXT, "+
                      TODO_DATE + " INTEGER, "+
                      TODO_TIME + " INTEGER, "+
                      TODO_STATUS + " INTEGER DEFAULT 0, " +
                      TODO_PENDING_INTENT_ID + " INTEGER DEFAULT 0, " +
                      TODO_ALARM_STATUS + " INTEGER DEFAULT 0 "+
                ");";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
