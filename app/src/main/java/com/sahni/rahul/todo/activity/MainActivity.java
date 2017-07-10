package com.sahni.rahul.todo.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.adapter.TodoRecyclerAdapter;
import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.IntentConstants;
import com.sahni.rahul.todo.interfaces.CheckBoxClickedListener;
import com.sahni.rahul.todo.interfaces.TodoViewHolderClickListener;
import com.sahni.rahul.todo.models.TodoClass;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TodoViewHolderClickListener, CheckBoxClickedListener {

    public static final int ADD_REQ_CODE = 1;
    public static final int EDIT_REQ_CODE = 2;
    public static final String TODO_SHARED_PREF = "todo_shared_pref";
    public static final String TITLE_SHARED_PREF_KEY = "title_key";
    public static final String DATE_SHARED_PREF_KEY = "Date_key";

    //    ListView todoListView;
    ArrayList<TodoClass> todoArrayList;
    //    TodoListArrayAdapter todoListArrayAdapter;
    TextView noTodoTextView;
    View bottomSheetView;

    RecyclerView todoRecyclerView;
    TodoRecyclerAdapter todoAdapter;


    ArrayList<String> spinnerList;
    Spinner spinner;


    boolean isSharedPrefEmpty = true;

    ArrayList<TodoClass> categoryArrayList;




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


//        todoListView = (ListView) findViewById(R.id.activity_main_list_view);

        todoRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        todoAdapter = new TodoRecyclerAdapter(this, todoArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        todoRecyclerView.addItemDecoration(itemDecoration);
        todoRecyclerView.setAdapter(todoAdapter);


        noTodoTextView = (TextView) findViewById(R.id.no_todo_text_view);

//        todoListArrayAdapter = new TodoListArrayAdapter(this, todoArrayList);
//        todoListView.setAdapter(todoListArrayAdapter);
//        todoListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);


        spinner = (Spinner) findViewById(R.id.main_spinner);
        spinnerList = new ArrayList<>();
        spinnerList.add("All");
        spinnerList.add("General");
        spinnerList.add("Personal");
        spinnerList.add("Home");
        spinnerList.add("Work");
        spinnerList.add("Finished");
        final ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerList);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, spinnerList.get(position) + "selected", Toast.LENGTH_SHORT).show();
                showSelectedCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//
//
//                TodoClass todo = todoArrayList.get(position);
//
//                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
//                intent.putExtra(IntentConstants.REQ_KEY, EDIT_REQ_CODE);
//                intent.putExtra(IntentConstants.TODO_TITLE, todo.getTitle());
//                intent.putExtra(IntentConstants.TODO_DATE, todo.getDate());
//                intent.putExtra(IntentConstants.TODO_ID, todo.getId());
//                intent.putExtra(IntentConstants.TODO_STATUS, todo.getStatus());
//
//                startActivityForResult(intent, EDIT_REQ_CODE);
//            }
//        });
//
//        todoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure you want to delete?");
//                builder.setCancelable(false);
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        TodoClass todo = todoArrayList.get(position);
//                        int todoId = todo.getId();
//                        deleteRowFromList(todoId);
////                        View view = (View) spinner.getSelectedItem();
////                        fetchAllTodo();
////                        if(spinner.getSelectedItemPosition() == 0){
////                            fetchAllTodo();
////                        }
////                        else{
////                            spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
////                        }
//                        showSelectedCategory(spinner.getSelectedItemPosition());
//                        todoListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
//
//                    }
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.create().show();
//
//                return true;
//            }
//        });
//
//
//        todoListArrayAdapter.setCheckBoxClickedListener(this);


        todoAdapter.setViewHolderClickListener(this);
        todoAdapter.setCheckBoxClickedListener(this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
                intent.putExtra(IntentConstants.REQ_KEY, ADD_REQ_CODE);
                startActivityForResult(intent, ADD_REQ_CODE);

            }
        });

    }


    private void showSelectedCategory(int position) {

        boolean isQueryEmpty = true;


        if (position == 0) {
            fetchAllTodo();
            return;
        } else if (spinnerList.get(position).equalsIgnoreCase("Finished")) {
            todoArrayList.clear();
            SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_STATUS + " = " + TodoOpenHelper.DONE, null, null, null, null);
            while (cursor.moveToNext()) {
                isQueryEmpty = false;
                int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
                String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
                long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));
                todoArrayList.add(new TodoClass(id, task, date, time, category, status));
            }
            cursor.close();
        } else {
            todoArrayList.clear();
            SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
            String argument[] = {spinnerList.get(position), "" + TodoOpenHelper.NOT_DONE};
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_CATEGORY + " = ? AND " + TodoOpenHelper.TODO_STATUS + " = ?",
                    argument, null, null, null);
            while (cursor.moveToNext()) {
                isQueryEmpty = false;
                int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
                String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
                long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));
                todoArrayList.add(new TodoClass(id, task, date, time, category, status));
            }
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

//        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
//        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_CATEGORY + " = "+ spinnerList.get(position)
//        , null, null, null,null);
//        todoArrayList.clear();
//
//        while(cursor.moveToNext()){
//            String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
//            int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
//            long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
//            long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
//            todoArrayList.add(new TodoClass(id,title, date, time));
//        }

//        todoListArrayAdapter.notifyDataSetChanged();
    }


    private void deleteRowFromList(int todoId) {
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getWritableDatabase();
        database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_ID + " = " + todoId, null);
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.menu_add) {
//            Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
//            intent.putExtra(IntentConstants.REQ_KEY, ADD_REQ_CODE);
//            startActivityForResult(intent, ADD_REQ_CODE);
//        }
//
//
//        return true;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("onActivityResult_tag", "in onActivty");
        if (requestCode == ADD_REQ_CODE && resultCode == RESULT_OK) {


            noTodoTextView.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);
            addNewTodo();
            spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));
            todoRecyclerView.scrollToPosition(todoAdapter.getItemCount());
//            todoRecyclerView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

//
//            String title = data.getStringExtra(IntentConstants.TODO_TITLE);
//            String date = data.getStringExtra(IntentConstants.TODO_DATE);
//            todoArrayList.add(todoArrayList.size(), new TodoClass(title, date));
//            todoListArrayAdapter.notifyDataSetChanged();
//            SharedPreferences sharedPreferences = getSharedPreferences(TODO_SHARED_PREF, MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString(TITLE_SHARED_PREF_KEY,sharedPreferences.getString(TITLE_SHARED_PREF_KEY,"")+title+";");
//            editor.putString(DATE_SHARED_PREF_KEY, sharedPreferences.getString(DATE_SHARED_PREF_KEY,"")+date+";");
//            editor.apply();
        } else if (requestCode == EDIT_REQ_CODE && resultCode == RESULT_OK) {
            fetchAllTodo();
            spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));
//            todoRecyclerView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);


        }
    }

//    private void updateSharedPref() {
//        String title = "";
//        String date = "";
//        SharedPreferences.Editor editor = getSharedPreferences(TODO_SHARED_PREF, MODE_PRIVATE).edit();
//        if (todoArrayList.isEmpty()) {
//            editor.clear().apply();
//            isSharedPrefEmpty = true;
//
//        } else {
//            for (int i = 0; i < todoArrayList.size(); i++) {
//                TodoClass todoClass = todoArrayList.get(i);
//                title = title + todoClass.getTitle() + ";";
//                date = date + todoClass.getDate() + ";";
//            }
//            editor.putString(TITLE_SHARED_PREF_KEY, title);
//            editor.putString(DATE_SHARED_PREF_KEY, date);
//            editor.apply();
//        }
//
//    }


    private void fetchAllTodo() {

        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();
        todoArrayList.clear();
        todoAdapter.notifyDataSetChanged();

//        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM TABLE "+TodoOpenHelper.TODO_TABLE, null);
//        cursor.moveToFirst();
        if (getNoOfDoneTodo(database) > 0) {
            todoArrayList.clear();
            noTodoTextView.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_STATUS + " = 0", null, null, null, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                String task = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));


                long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                long time = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
                String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                int status = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_STATUS));


                todoArrayList.add(new TodoClass(id, task, date, time, category, status));
                todoAdapter.notifyDataSetChanged();

            }
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

        todoArrayList.add(new TodoClass(id, task, date, time, category, TodoOpenHelper.NOT_DONE));
        todoAdapter.notifyDataSetChanged();
        cursor.close();

    }

    private void updateTodoList() {


    }

    private long getNoOfDoneTodo(SQLiteDatabase database) {


        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS + " = 0");

    }


    @Override
    public void checkBoxClicked(final CheckBox checkBox, int position) {

        TodoClass todo = todoArrayList.get(position);
        final int id = todo.getId();

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
                    cv.put(TodoOpenHelper.TODO_STATUS, TodoOpenHelper.DONE);
                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = " + id, null);
//                fetchAllTodo();
//                spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
//                    if(spinner.getSelectedItemPosition() == 0){
//                        fetchAllTodo();
//                    }
//                    else{
//                        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
//                    }
                    showSelectedCategory(spinner.getSelectedItemPosition());

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
                    SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(MainActivity.this).getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(TodoOpenHelper.TODO_STATUS, TodoOpenHelper.NOT_DONE);
                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = " + id, null);
//                fetchAllTodo();
//                spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
//                    if(spinner.getSelectedItemPosition() == 0){
//                        fetchAllTodo();
//                    }
//                    else{
//                        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
//                    }
                    showSelectedCategory(spinner.getSelectedItemPosition());

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
                deleteRowFromList(todoId);
//                        View view = (View) spinner.getSelectedItem();
//                        fetchAllTodo();
//                        if(spinner.getSelectedItemPosition() == 0){
//                            fetchAllTodo();
//                        }
//                        else{
//                            spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("All"));
//                        }
                showSelectedCategory(spinner.getSelectedItemPosition());
//                todoRecyclerView.(ListView.TRANSCRIPT_MODE_NORMAL);

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


}
