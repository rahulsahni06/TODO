package com.sahni.rahul.todo.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.broadcastReceivers.ShowNotificationReceiver;
import com.sahni.rahul.todo.database.DatabaseConstants;
import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.IntentConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.sahni.rahul.todo.database.DatabaseConstants.ALARM_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.ALARM_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.DATE_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.TIME_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_DONE;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_NOT_DONE;
import static com.sahni.rahul.todo.helpers.EpochToDateTime.convert;
import static com.sahni.rahul.todo.helpers.EpochToDateTime.convertTime;
import static com.sahni.rahul.todo.helpers.SharedPrefConstants.PENDING_INTENT_ID;
import static com.sahni.rahul.todo.helpers.SharedPrefConstants.PENDING_INTENT_ID_PREF;

public class AddTodoActivity extends AppCompatActivity {

    Button button;
    EditText titleEditText;
    EditText dateEditText;
    EditText timeEditText;

    Spinner categorySpinner;

    LinearLayout timeLayout;

    CheckBox statusCheckBox;

    ImageView calendarCancelImageView;
    ImageView timeCancelImageView;
    TextView infoAlarmTextView;

    Calendar calendar;

    long dateInEpoch = DATE_NOT_SET , timeInEpoch= TIME_NOT_SET;
    String category;

    boolean isNewCategoryAdded = false;
    ArrayList<String> spinnerList;
    ArrayAdapter spinnerArrayAdapter;

    @Override
    public void onBackPressed() {
        Log.i("back button", "back pressed");
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        button = (Button) findViewById(R.id.submit_button);
        titleEditText = (EditText) findViewById(R.id.title_add_edit_text);
        dateEditText  = (EditText) findViewById(R.id.date_add_edit_text);
        timeEditText = (EditText) findViewById(R.id.time_add_edit_text);
        infoAlarmTextView = (TextView) findViewById(R.id.info_set_alarm_text_view);
        final ImageView calendarImageView = (ImageView) findViewById(R.id.calendar_image_view);
        ImageView timeImageView = (ImageView) findViewById(R.id.time_image_view);
        calendarCancelImageView = (ImageView) findViewById(R.id.date_cancel_image_view);
        timeCancelImageView = (ImageView) findViewById(R.id.time_cancel_image_view);
        timeLayout = (LinearLayout) findViewById(R.id.time_linear_layout);

        statusCheckBox = (CheckBox) findViewById(R.id.task_finished_checkbox);
        ImageView newCategoryImageView = (ImageView) findViewById(R.id.new_category_image_view);
        categorySpinner = (Spinner) findViewById(R.id.add_todo_spinner);

        spinnerList = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        updateSpinnerArrayList();



        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = spinnerList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        newCategoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.add_new_category_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoActivity.this)
                        .setView(dialogView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isNewCategoryAdded = false;
                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) dialogView.findViewById(R.id.add_category_edit_text);
                        String newCategory = editText.getText().toString();
                        if(newCategory.trim().equals("")){
                            editText.setError("Please enter valid category");
                        }
                        else{
                            spinnerList.add(newCategory);
                            spinnerArrayAdapter.notifyDataSetChanged();
                            categorySpinner.setSelection(spinnerList.size());
                            category = newCategory;
                            isNewCategoryAdded = true;
                            dialog.dismiss();
                        }
                    }
                });





            }
        });

        final Intent intent = getIntent();
        final int reqCode = intent.getIntExtra(IntentConstants.REQ_KEY, 10);
        if(reqCode == MainActivity.ADD_REQ_CODE){
            setTitle("Add");
        }
        else{
            setTitle("Edit");
            String title = intent.getStringExtra(IntentConstants.TODO_TITLE);
            dateInEpoch = intent.getLongExtra(IntentConstants.TODO_DATE, 0);
            int status = intent.getIntExtra(IntentConstants.TODO_STATUS, -1);
            infoAlarmTextView.setVisibility(View.GONE);


            if(status == TODO_DONE){
                statusCheckBox.setVisibility(View.VISIBLE);
                statusCheckBox.setChecked(true);
            }

            titleEditText.setText(title);
            dateEditText.setText(convert(dateInEpoch));

            calendarCancelImageView.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.VISIBLE);

            calendar = Calendar.getInstance();
            calendar.setTime(new Date(dateInEpoch));
            Log.i("Edit_req", "date = "+new Date(dateInEpoch));
        }

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();

            }
        });

        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        calendarCancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateInEpoch = DATE_NOT_SET ;
                timeInEpoch = TIME_NOT_SET;
//                dateEditText.setBackground(ContextCompat.getDrawable(AddTodoActivity.this, android.R.color.darker_gray));
                dateEditText.setText("");
                dateEditText.setHint("Date not set");
                timeEditText.setText("");
                infoAlarmTextView.setVisibility(View.VISIBLE);
                calendarCancelImageView.setVisibility(View.GONE);
                timeLayout.setVisibility(View.GONE);

            }
        });

        timeCancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInEpoch = TIME_NOT_SET;
                timeLayout.setVisibility(View.GONE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleEditText.getText().toString();

                SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(AddTodoActivity.this).getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(TodoOpenHelper.TODO_TASK, title);
                cv.put(TodoOpenHelper.TODO_CATEGORY, category);
                cv.put(TodoOpenHelper.TODO_DATE, dateInEpoch);
                cv.put(TodoOpenHelper.TODO_TIME, timeInEpoch);

                Intent sendResultIntent = new Intent();
                sendResultIntent.putExtra(IntentConstants.NEW_CATEGORY, isNewCategoryAdded);


                if(reqCode == MainActivity.ADD_REQ_CODE){


                    if(title.trim().equals("")){
                        titleEditText.setError("Please enter title");
                    }
                    else{
                        Log.i("Add", "title= "+title);
                        Log.i("Add", "date= "+dateInEpoch);
                        Log.i("Add", "time= "+timeInEpoch);

                        if(timeInEpoch != TIME_NOT_SET){
                            int pId = getPendingIntentId();
                            cv.put(TodoOpenHelper.TODO_PENDING_INTENT_ID, pId);
                            cv.put(TodoOpenHelper.TODO_ALARM_STATUS, ALARM_SET);
                            setTodoNotification(pId, title);
                            setPendingIntentId(++pId);

                            Log.i("PENDING", "ID = "+pId);

                        }
                        database.insert(TodoOpenHelper.TODO_TABLE, null, cv);
                        setResult(RESULT_OK, sendResultIntent);
                        finish();
                    }


                }
                else if(reqCode == MainActivity.EDIT_REQ_CODE){

                    if(statusCheckBox.getVisibility() == View.VISIBLE){
                        if(statusCheckBox.isChecked()){
                            cv.put(TodoOpenHelper.TODO_STATUS, TODO_DONE);
                        }
                        else{
                            cv.put(TodoOpenHelper.TODO_STATUS, TODO_NOT_DONE);
                        }
                    }
                    int id = intent.getIntExtra(IntentConstants.TODO_ID, -1);

                    if(timeInEpoch != TIME_NOT_SET){
//                        cv.put(TodoOpenHelper.TODO_PENDING_INTENT_ID, ++pendingIntentId);
                        int pId = getPendingIntentId();
                        cv.put(TodoOpenHelper.TODO_PENDING_INTENT_ID, pId);
                        cv.put(TodoOpenHelper.TODO_ALARM_STATUS, ALARM_SET);
                        setTodoNotification(pId, title);
                        setPendingIntentId(++pId);

                    }
                    else{
                        cv.put(TodoOpenHelper.TODO_PENDING_INTENT_ID, 0);
                        cv.put(TodoOpenHelper.TODO_ALARM_STATUS, ALARM_NOT_SET);
                    }
                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = "+id, null);
                    setResult(RESULT_OK, sendResultIntent);
                    finish();
                }


            }
        });



    }


    private void setTodoNotification( int pendingIntentId, String title) {

        Log.i("notification", "ID = "+pendingIntentId);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ShowNotificationReceiver.class);
        intent.putExtra(IntentConstants.TODO_TITLE, title);
        intent.putExtra(IntentConstants.TODO_PENDING_ID, pendingIntentId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, 0);
        alarmManager.set(AlarmManager.RTC, timeInEpoch, pendingIntent);

    }


    private void showDatePicker() {

        calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        Log.i("Date picker", "Date = "+date);
        Log.i("Date picker", "month = "+month);
        Log.i("Date picker", "year = "+year);
        Log.i("Date picker", "TIMEXNE = "+ TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName()));


        DatePickerDialog dialog = new DatePickerDialog(AddTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Log.i("Date picker", "Date = "+dayOfMonth);
                Log.i("Date picker", "month = "+month);
                Log.i("Date picker", "year = "+year);

                try {
                    dateInEpoch = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse("" +
                            dayOfMonth +
                            "/" +
                            (month+1) +
                            "/" +
                            year +
                            " 00:00:00").getTime();
                    Log.i("DatePicker", "New date =" +dateInEpoch);
                } catch (ParseException e) {
                    e.printStackTrace();
                    dateInEpoch = calendar.getTime().getTime();
                    Log.i("DatePicker", "Error so using old date =" +dateInEpoch);
                }

//                dateInEpoch = calendar.getTime().getTime();
                Log.i("Date picker", "seconds = "+dateInEpoch);
//                TimeZone.getDefault().getDisplayName();
                dateEditText.setText(convert(dateInEpoch));

                calendarCancelImageView.setVisibility(View.VISIBLE);
                timeLayout.setVisibility(View.VISIBLE);
                timeEditText.setText("");
                infoAlarmTextView.setVisibility(View.GONE);

            }
        },year , month, date);

        dialog.show();

    }


    private void showTimePicker(){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hourOfDay, minute);
                Log.i("ShowTImePicker", "date ="+ new Date(calendar.getTime().getTime()));
                timeInEpoch = calendar.getTime().getTime();
                String time = convertTime(timeInEpoch);
                timeEditText.setText(time);
            }
        },12,0,false);
        dialog.show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Log.i("home", "home pressed");
            onBackPressed();
        }
        return  true;
    }


    public int getPendingIntentId(){
        SharedPreferences sharedPreferences = getSharedPreferences(PENDING_INTENT_ID_PREF, MODE_PRIVATE);
        return sharedPreferences.getInt(PENDING_INTENT_ID, 1);

    }

    public void setPendingIntentId(int id){
        SharedPreferences.Editor editor = getSharedPreferences(PENDING_INTENT_ID_PREF, MODE_PRIVATE).edit();
        editor.putInt(PENDING_INTENT_ID, id);
        editor.apply();
    }

    public void updateSpinnerArrayList(){
        spinnerList.addAll(DatabaseConstants.getSpinnerArrayList(this));
        spinnerList.remove("All");
        spinnerList.remove("Today");
        spinnerList.remove("Finished");
        spinnerList.remove("Overdue");
        spinnerArrayAdapter.notifyDataSetChanged();

    }
}
