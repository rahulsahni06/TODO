package com.sahni.rahul.todo.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


    ArrayList<TodoClass> todoArrayList;
    TextView noTodoTextView;
    View bottomSheetView;

    RecyclerView todoRecyclerView;
    TodoRecyclerAdapter todoAdapter;


    ArrayList<String> spinnerList;
    Spinner spinner;


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
        todoRecyclerView.setAdapter(todoAdapter);



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
            todoArrayList.add(new TodoClass(id, task, date, time, category, status));
        }
        return isQueryEmpty;

    }


    private void showSpinnerSelectedCategory(int position) {

        boolean isQueryEmpty;
        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(this).getReadableDatabase();


        if (position == 0) {
            fetchAllTodo();
            return;
        } else if (spinnerList.get(position).equalsIgnoreCase("Finished")) {
            todoArrayList.clear();
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_STATUS + " = " + TodoOpenHelper.DONE, null, null, null, null);
            isQueryEmpty = readDataAfterQuery(cursor);
            cursor.close();
        } else {
            todoArrayList.clear();

            String argument[] = {spinnerList.get(position), "" + TodoOpenHelper.NOT_DONE};
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.menu_delete_all) {
            deleteRowByCategory("All");

        }else if(id == R.id.menu_delete_general){
            deleteRowByCategory("General");

        }else if(id == R.id.menu_delete_personal){
            deleteRowByCategory("Personal");


        }else if(id == R.id.menu_delete_home){
            deleteRowByCategory("Home");

        }else if(id == R.id.menu_delete_work){
            deleteRowByCategory("Work");


        }else if(id == R.id.menu_delete_finished){
            deleteRowByCategory("Finished");


        }

        return true;
    }


    private void showDeleteCategoryAlert(final String category, final SQLiteDatabase database){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete "+"\""+category+"\" list?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(category.equalsIgnoreCase("Finished")){

                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = "+ TodoOpenHelper.DONE,null);

                }
                else if(category.equalsIgnoreCase("All")){
                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = "+ TodoOpenHelper.NOT_DONE,null);
                }
                else{

                    String arguments[] = {""+TodoOpenHelper.NOT_DONE, category };

                    database.delete(TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = ? AND "+ TodoOpenHelper.TODO_CATEGORY + " = ?",arguments);

                }

                Snackbar.make(todoRecyclerView, category+" List deleted", Snackbar.LENGTH_SHORT ).show();
                showSpinnerSelectedCategory(spinner.getSelectedItemPosition());

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

        Log.d("onActivityResult_tag", "in onActivty");
        if (requestCode == ADD_REQ_CODE && resultCode == RESULT_OK) {

            noTodoTextView.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);
            addNewTodo();
            spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));
            todoRecyclerView.scrollToPosition(todoAdapter.getItemCount());

        } else if (requestCode == EDIT_REQ_CODE && resultCode == RESULT_OK) {

            fetchAllTodo();
            spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition("All"));

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
        todoArrayList.add(new TodoClass(id, task, date, time, category, TodoOpenHelper.NOT_DONE));
        todoAdapter.notifyDataSetChanged();
        cursor.close();

    }



    private long getNoOfTodoNotDone(SQLiteDatabase database) {


        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS + " = "+TodoOpenHelper.NOT_DONE);

    }

    private long getNoOfTodoDone(SQLiteDatabase database){
        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS + " = "+TodoOpenHelper.DONE);
    }

    private long getNoOfTodoInCategory(SQLiteDatabase database, String category){

        String arguments[] = {""+TodoOpenHelper.NOT_DONE, category };
        return DatabaseUtils.queryNumEntries(database, TodoOpenHelper.TODO_TABLE, TodoOpenHelper.TODO_STATUS+" = ? AND "+ TodoOpenHelper.TODO_CATEGORY + " = ?",arguments);

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
                deleteRowFromList(todoId);
                showSpinnerSelectedCategory(spinner.getSelectedItemPosition());

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




