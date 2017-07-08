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





    public TodoClass(int id, String title, long date, long time, String category, int status){
        this.title = title;
        this.date = date;
        this.id = id;
        this.time = time;
        this.category = category;
        this.status = status;
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

}
