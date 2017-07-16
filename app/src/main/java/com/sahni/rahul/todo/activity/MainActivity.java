package com.sahni.rahul.todo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.adapter.TodoRecyclerAdapter;
import com.sahni.rahul.todo.broadcastReceivers.ShowNotificationReceiver;
import com.sahni.rahul.todo.database.DatabaseConstants;
import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.EpochToDateTime;
import com.sahni.rahul.todo.helpers.IntentConstants;
import com.sahni.rahul.todo.interfaces.CheckBoxClickedListener;
import com.sahni.rahul.todo.interfaces.TodoViewHolderClickListener;
import com.sahni.rahul.todo.models.TodoClass;

import java.util.ArrayList;

import static com.sahni.rahul.todo.database.DatabaseConstants.ALARM_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.ALARM_OVER;
import static com.sahni.rahul.todo.database.DatabaseConstants.DATE_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_DONE;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_NOT_DONE;
import static com.sahni.rahul.todo.helpers.SharedPrefConstants.PENDING_INTENT_ID_PREF;

public class MainActivity extends AppCompatActivity implements TodoViewHolderClickListener, CheckBoxClickedListener {

    public static final int ADD_REQ_CODE = 1;
    public static final int EDIT_REQ_CODE = 2;


    ArrayList<TodoClass> todoArrayList;
    TextView noTodoTextView;

    RecyclerView todoRecyclerView;
    TodoRecyclerAdapter todoAdapter;


    ArrayList<String> spinnerList;
    Spinner spinner;
    ArrayAdapter spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onActivityResult_tag", "in OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Todo");

        }

        todoArrayList = new ArrayList<>();

        spinner = (Spinner) findViewById(R.id.main_spinner);
        noTodoTextView = (TextView) findViewById(R.id.no_todo_text_view);
        todoRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        todoAdapter = new TodoRecyclerAdapter(this, todoArrayList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        todoRecyclerView.addItemDecoration(itemDecoration);
        todoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        todoRecyclerView.setAdapter(todoAdapter);



        spinnerList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerList);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinnerArrayList();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showSpinnerSelectedCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        todoAdapter.setViewHolderClickListener(this);
        todoAdapter.setCheckBoxClickedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
                intent.putExtra(IntentConstants.REQ_KEY, ADD_REQ_CODE);
                startActivityForResult(intent, ADD_REQ_CODE);

            }
        });

    }


    private boolean readDataAfterQuery(Cursor cursor){

        boolean isQueryEmpty = true;

        while (cursor.moveToNext()) {
            isQueryEmpty = false;
            int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
            String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
            String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
            long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
            long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
            int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));
            int pendingIntentId = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
            int alarmStatus = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ALARM_STATUS));
            todoArrayList.add(new TodoClass(id, task, date, time, category, status, pendingIntentId, alarmStatus));
        }
        return isQueryEmpty;

    }


    private void showSpinnerSelectedCategory(int position) {

        boolean isQueryEmpty = true;
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();


        if (spinnerList.get(position).equalsIgnoreCase("All")) {
            fetchAllTodo();
            return;
        } else if (spinnerList.get(position).equalsIgnoreCase("Finished")) {
            todoArrayList.clear();
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_STATUS + " = " + TODO_DONE, null, null, null, null);
            isQueryEmpty = readDataAfterQuery(cursor);
            cursor.close();
        }
        else if(spinnerList.get(position).equalsIgnoreCase("Overdue")){
            todoArrayList.clear();
            long currentTime = System.currentTimeMillis();
            Log.i("Spinner", "time = "+currentTime);

            String args[] = {""+currentTime, ""+DATE_NOT_SET, ""+TODO_NOT_DONE, ""+ALARM_OVER, ""+ALARM_NOT_SET};

            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null,
                    TodoOpenHelper.TODO_DATE + " < ? AND "
                    +TodoOpenHelper.TODO_DATE+ " != ? AND "
                    +TodoOpenHelper.TODO_STATUS+ " = ? AND ( "
                    +TodoOpenHelper.TODO_ALARM_STATUS+ " = ? OR "
                    +TodoOpenHelper.TODO_ALARM_STATUS+ " = ? )",
                    args, null, null, null);
            while(cursor.moveToNext()){
                long epochSeconds = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                int alarmStatus = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ALARM_STATUS));
                if(!EpochToDateTime.checkTodayDate(epochSeconds ) || (EpochToDateTime.checkTodayDate(epochSeconds ) && alarmStatus == ALARM_OVER ) ){
                    isQueryEmpty = false;
                    int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                    String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
                    String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                    long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                    long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
                    int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));
                    int pendingIntentId = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
                    todoArrayList.add(new TodoClass(id, task, date, time, category, status, pendingIntentId, alarmStatus));

                }
            }
            cursor.close();
        }
        else if(spinnerList.get(position).equalsIgnoreCase("Today")){
            todoArrayList.clear();
            long currentTime = System.currentTimeMillis();
            Log.i("Spinner", "time = "+currentTime);
            String args[] = {""+DATE_NOT_SET, ""+TODO_NOT_DONE};
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null,TodoOpenHelper.TODO_DATE+ " != ? AND "+TodoOpenHelper.TODO_STATUS+ " = ?", args, null, null, null);
            while(cursor.moveToNext()){
                long epochSeconds = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                if(EpochToDateTime.checkTodayDate(epochSeconds)){
                    isQueryEmpty = false;
                    int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                    String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
                    String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                    long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                    long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
                    int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));
                    int pendingIntentId = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
                    int alarmStatus = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ALARM_STATUS));
                    todoArrayList.add(new TodoClass(id, task, date, time, category, status, pendingIntentId, alarmStatus));

                }
            }
            todoAdapter.notifyDataSetChanged();
//            isQueryEmpty = readDataAfterQuery(cursor);
            cursor.close();
        }

        else {
            todoArrayList.clear();

            String argument[] = {spinnerList.get(position), "" + TODO_NOT_DONE};
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_CATEGORY + " = ? AND " + TodoOpenHelper.TODO_STATUS + " = ?",
                    argument, null, null, null);
            isQueryEmpty = readDataAfterQuery(cursor);
            cursor.close();

        }

        todoAdapter.notifyDataSetChanged();

        if (isQueryEmpty) {
            todoRecyclerView.setVisibility(View.GONE);
            noTodoTextView.setVisibility(View.VISIBLE);
        } else {
            noTodoTextView.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);


        }


    }


    private void deleteRowFromList(int todoId) {
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getWritableDatabase();
        database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_ID + " = " + todoId, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.i("prepare", "create menu called");
////        getMenuInflater().inflate(R.menu.main_menu, menu);
//        SubMenu subMenu = menu.addSubMenu("Delete");
//        for(String category : spinnerList){
//            if(!category.equalsIgnoreCase("Today") && !category.equalsIgnoreCase("Overdue")){
//                subMenu.add(category);
//            }
//        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        for(int i =0; i<spinnerList.size(); i++){
            String category = spinnerList.get(i);
            if(id == spinnerList.indexOf(category) && (!category.equals("Today") || (!category.equals("Overdue")))){
                deleteRowByCategory(category);
            }
        }
        return true;
    }


    private void showDeleteCategoryAlert(final String category, final SQLiteDatabase database){

        final boolean isCustomColumn[] = {true};

        final String column[] = {TodoOpenHelper.TODO_PENDING_INTENT_ID};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete "+"\""+category+"\" list?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(category.equalsIgnoreCase("Finished")){


                    Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, column,TodoOpenHelper.TODO_PENDING_INTENT_ID+ " != 0 AND "+ TodoOpenHelper.TODO_STATUS+" = "+TODO_DONE, null, null, null, null);
                    while(cursor.moveToNext()){
                        int pendingInt = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
                        cancelNotification(pendingInt);
                    }
                    cursor.close();
                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = "+ TODO_DONE,null);

                }
                else if(category.equalsIgnoreCase("All")){
                    Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, column,TodoOpenHelper.TODO_PENDING_INTENT_ID+ " != 0", null, null, null, null);
                    while(cursor.moveToNext()){
                        int pendingInt = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
                        cancelNotification(pendingInt);
                    }
                    cursor.close();
                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = "+ TODO_NOT_DONE,null);
                    deleteStoredPendingIntentId();

                }
                else{
                    if(category.equals("General") || category.equals("Personal") || category.equals("Home")
                    || category.equals("Work")){
                        isCustomColumn[0] = false;
                    }

                    String arguments[] = {""+TODO_NOT_DONE, category };
                    Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null,TodoOpenHelper.TODO_PENDING_INTENT_ID+ " != 0 AND "+ TodoOpenHelper.TODO_CATEGORY+" = '"+category+"'", null, null, null, null);
                    while(cursor.moveToNext()){
                        int pendingInt = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
                        cancelNotification(pendingInt);
                    }
                    cursor.close();

                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = ? AND "+ TodoOpenHelper.TODO_CATEGORY + " = ?",arguments);

                }
                if(isCustomColumn[0]) {
                    int size = spinnerList.size();
                    spinnerList.clear();
                    updateSpinnerArrayList();
                    spinnerAdapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
//
                }
                    try{
                        showSpinnerSelectedCategory(spinner.getSelectedItemPosition());
                    }catch (Exception e){
                        e.printStackTrace();
                        spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));
                    }

                Snackbar.make(todoRecyclerView, category+" List deleted", Snackbar.LENGTH_SHORT ).show();


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }



    private void deleteRowByCategory(String category) {


        final SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
        if(category.equalsIgnoreCase("Finished")){
            if(getNoOfTodoDone(database) > 0){
                showDeleteCategoryAlert("Finished", database);

            }else{
                Snackbar.make(todoRecyclerView, "List Empty", Snackbar.LENGTH_SHORT ).show();
            }
        }else if(category.equalsIgnoreCase("All")){
            if(getNoOfTodoNotDone(database) > 0){
                showDeleteCategoryAlert("All", database);


            }else{
                Snackbar.make(todoRecyclerView, "List Empty", Snackbar.LENGTH_SHORT ).show();
            }
        }else{
            if(getNoOfTodoInCategory(database, category) > 0){
                showDeleteCategoryAlert(category, database);
            }
            else{
                Snackbar.make(todoRecyclerView, "List Empty", Snackbar.LENGTH_SHORT ).show();
            }
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){

            boolean isNewCategoryAdded = data.getBooleanExtra(IntentConstants.NEW_CATEGORY, false);
            if(isNewCategoryAdded){
                updateSpinnerArrayList();

                invalidateOptionsMenu();
            }
            if (requestCode == ADD_REQ_CODE) {

                noTodoTextView.setVisibility(View.GONE);
                todoRecyclerView.setVisibility(View.VISIBLE);
                addNewTodo();
                spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));
                todoRecyclerView.smoothScrollToPosition(todoArrayList.size());

            } else if (requestCode == EDIT_REQ_CODE) {

                fetchAllTodo();
                spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));

            }
        }

    }


    private void fetchAllTodo() {

        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
        todoArrayList.clear();
        todoAdapter.notifyDataSetChanged();

        if (getNoOfTodoNotDone(database) > 0) {
            todoArrayList.clear();
            noTodoTextView.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_STATUS + " = 0", null, null, null, null);
            readDataAfterQuery(cursor);
            todoAdapter.notifyDataSetChanged();
            cursor.close();
        } else {
            todoRecyclerView.setVisibility(View.GONE);
            noTodoTextView.setVisibility(View.VISIBLE);

        }

    }


    private void addNewTodo() {
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, null, null, null, null, null);
        cursor.moveToLast();
        int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
        String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
        long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
        long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
        String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
        int pendingIntentId = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));
        int alarmStatus = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ALARM_STATUS));
        todoArrayList.add(new TodoClass(id, task, date, time, category, TODO_NOT_DONE, pendingIntentId, alarmStatus));
        todoAdapter.notifyDataSetChanged();
        cursor.close();

    }



    private long getNoOfTodoNotDone(SQLiteDatabase database) {


        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS + " = "+TODO_NOT_DONE);

    }

    private long getNoOfTodoDone(SQLiteDatabase database){
        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS + " = "+TODO_DONE);
    }

    private long getNoOfTodoInCategory(SQLiteDatabase database, String category){

        String arguments[] = {""+TODO_NOT_DONE, category };
        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = ? AND "+ TodoOpenHelper.TODO_CATEGORY + " = ?",arguments);

    }


    @Override
    public void checkBoxClicked(final CheckBox checkBox, int position) {

        final TodoClass todo = todoArrayList.get(position);
        final int id = todo.getId();
        final int pendingId = todo.getPendingIntentId();

        Log.i("listAdapter", "checked fun =" + checkBox.isChecked());


        if (checkBox.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit");
            builder.setMessage("Task finished?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(MainActivity.this).getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(TodoOpenHelper.TODO_STATUS, TODO_DONE);
                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = " + id, null);
                    if(pendingId != 0){
                        cancelNotification(pendingId);
                    }

                    showSpinnerSelectedCategory(spinner.getSelectedItemPosition());

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkBox.setChecked(false);
                    dialog.dismiss();
                }
            });

            builder.create().show();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit");
            builder.setMessage("Task still pending?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title = todo.getTitle();
                    long timeInEpoch = todo.getTime();

                    SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(MainActivity.this).getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(TodoOpenHelper.TODO_STATUS, TODO_NOT_DONE);

                    if(pendingId != 0){
                        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, ShowNotificationReceiver.class);
                        intent.putExtra(IntentConstants.TODO_TITLE, title);
                        intent.putExtra(IntentConstants.TODO_PENDING_ID, pendingId);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, pendingId, intent, 0);
                        alarmManager.set(AlarmManager.RTC, timeInEpoch, pendingIntent);
                    }

                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = " + id, null);
                    showSpinnerSelectedCategory(spinner.getSelectedItemPosition());

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkBox.setChecked(true);
                    dialog.dismiss();
                }
            });

            builder.create().show();

        }


    }


    @Override
    public void onTodoViewHolderClicked(View view) {
        int position = todoRecyclerView.getChildLayoutPosition(view);

        TodoClass todo = todoArrayList.get(position);

        Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
        intent.putExtra(IntentConstants.REQ_KEY, EDIT_REQ_CODE);
        intent.putExtra(IntentConstants.TODO_TITLE, todo.getTitle());
        intent.putExtra(IntentConstants.TODO_DATE, todo.getDate());
        intent.putExtra(IntentConstants.TODO_ID, todo.getId());
        intent.putExtra(IntentConstants.TODO_STATUS, todo.getStatus());

        startActivityForResult(intent, EDIT_REQ_CODE);
    }

    @Override
    public void onTodoViewHolderLongClicked(View view) {
        final int position = todoRecyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoClass todo = todoArrayList.get(position);
                int todoId = todo.getId();
                int pendingIntentId = todo.getPendingIntentId();
                deleteRowFromList(todoId);
                showSpinnerSelectedCategory(spinner.getSelectedItemPosition());
                cancelNotification(pendingIntentId);


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();


    }

    private void cancelNotification(int pendingIntentId) {

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ShowNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, 0);
        alarmManager.cancel(pendingIntent);
    }





    private void deleteStoredPendingIntentId(){
        SharedPreferences.Editor editor = getSharedPreferences(PENDING_INTENT_ID_PREF, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

    }

    private BroadcastReceiver notificationActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showSpinnerSelectedCategory(spinner.getSelectedItemPosition());
            Snackbar.make(todoRecyclerView,"Todo marked as finished", Snackbar.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConstants.MARK_TODO_AS_DONE_ACTION);
        registerReceiver(this.notificationActionReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(this.notificationActionReceiver);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Log.i("prepare", "prepare menu called");

        menu.clear();

        SubMenu subMenu = menu.addSubMenu(0, -1, 0,"Delete");
        for(String category : spinnerList){
            if(!category.equalsIgnoreCase("Today") && !category.equalsIgnoreCase("Overdue")){
                subMenu.add(0,spinnerList.indexOf(category),0,category);
            }
        }

        return true;
    }

    void updateSpinnerArrayList(){
        spinnerList.clear();
        spinnerList.addAll(DatabaseConstants.getSpinnerArrayList(this));
        spinnerAdapter.notifyDataSetChanged();
    }
}




