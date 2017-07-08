package com.sahni.rahul.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sahni on 28-Jun-17.
 */

public class TodoOpenHelper extends SQLiteOpenHelper {

    public static String TODO_ID = "id";
    public static String TODO_TABLE = "todo";
    public static String TODO_TASK = "task";
    public static String TODO_CATEGORY = "category";
    public static String TODO_DATE = "date";
    public static String TODO_TIME = "time";
    public static String TODO_STATUS = "status";

    public static int NOT_DONE = 0;
    public static int DONE = 1;




    /**
     * Singleton class, its object can only be created inside the class
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
                      TODO_STATUS + " INTEGER DEFAULT 0);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
