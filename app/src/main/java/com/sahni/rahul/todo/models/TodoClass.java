package com.sahni.rahul.todo.models;

/**
 * Created by sahni on 24-Jun-17.
 */

public class TodoClass {

    private int id;
    private String title;
    private long date;
    private long time;
    private String category;
    private int status;
    private int pendingIntentId;
    private int alarmStatus;

    public TodoClass(int id, String title, long date, long time, String category, int status, int pendingIntentId, int alarmStatus) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;
        this.status = status;
        this.pendingIntentId = pendingIntentId;
        this.alarmStatus = alarmStatus;
    }


    public String getTitle() {
        return title;
    }



    public long getDate() {
        return date;
    }


    public int getId() {
        return id;
    }


    public long getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public int getStatus() {
        return status;
    }

    public int getPendingIntentId() {
        return pendingIntentId;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }
}
