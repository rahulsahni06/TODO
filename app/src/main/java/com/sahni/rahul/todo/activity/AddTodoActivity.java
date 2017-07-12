package com.sahni.rahul.todo.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.sahni.rahul.todo.helpers.IntentConstants;
import com.sahni.rahul.todo.broadcastReceivers.MyTodoReceiver;
import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.database.TodoOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sahni.rahul.todo.helpers.EpochToDateTime.NO_DATE_SELECTED;
import static com.sahni.rahul.todo.helpers.EpochToDateTime.NO_TIME_SELECTED;
import static com.sahni.rahul.todo.helpers.EpochToDateTime.convert;
import static com.sahni.rahul.todo.helpers.EpochToDateTime.convertTime;

public class AddTodoActivity extends AppCompatActivity {
    static int i =0;

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

    boolean isDateSet = false;

    Calendar calendar;

    long dateInEpoch = NO_DATE_SELECTED , timeInEpoch= NO_TIME_SELECTED;
    String category;

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

        final ArrayList<String> spinnerList = new ArrayList<>();
        spinnerList.add("General");
        spinnerList.add("Personal");
        spinnerList.add("Home");
        spinnerList.add("Work");

        categorySpinner = (Spinner) findViewById(R.id.add_todo_spinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = spinnerList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


            if(status == TodoOpenHelper.DONE){
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
                dateInEpoch = NO_DATE_SELECTED ;
                timeInEpoch = NO_TIME_SELECTED;
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
                timeInEpoch = NO_TIME_SELECTED;
                timeLayout.setVisibility(View.GONE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleEditText.getText().toString();
                String date = dateEditText.getText().toString();

                SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(AddTodoActivity.this).getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(TodoOpenHelper.TODO_TASK, title);
                cv.put(TodoOpenHelper.TODO_CATEGORY, category);
                cv.put(TodoOpenHelper.TODO_DATE, dateInEpoch);
                cv.put(TodoOpenHelper.TODO_TIME, timeInEpoch);


                if(reqCode == MainActivity.ADD_REQ_CODE){


                    if(title.trim().equals("")){
                        titleEditText.setError("Please enter title");
                    }
                    else{
                        Log.i("Add", "title= "+title);
                        Log.i("Add", "date= "+dateInEpoch);
                        Log.i("Add", "time= "+timeInEpoch);
                        database.insert(TodoOpenHelper.TODO_TABLE, null, cv);
                        setTodoNotification(timeInEpoch, title);
                        setResult(RESULT_OK);
                        finish();
                    }


                }
                else if(reqCode == MainActivity.EDIT_REQ_CODE){

                    if(statusCheckBox.getVisibility() == View.VISIBLE){
                        if(statusCheckBox.isChecked()){
                            cv.put(TodoOpenHelper.TODO_STATUS, TodoOpenHelper.DONE);
                        }
                        else{
                            cv.put(TodoOpenHelper.TODO_STATUS, TodoOpenHelper.NOT_DONE);
                        }
                    }
                    int id = intent.getIntExtra(IntentConstants.TODO_ID, -1);
                    database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_ID + " = "+id, null);
                    setTodoNotification(timeInEpoch, title);
                    setResult(RESULT_OK);
                    finish();
                }


            }
        });



    }


    private void setTodoNotification(long milliSec, String title){
        if(milliSec != NO_TIME_SELECTED){
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, MyTodoReceiver.class);
            intent.putExtra(IntentConstants.TODO_TITLE, title);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i++, intent, 0);
            alarmManager.set(AlarmManager.RTC, timeInEpoch, pendingIntent);
        }
    }


    private void showDatePicker() {

        calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        DatePickerDialog dialog = new DatePickerDialog(AddTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                dateInEpoch = calendar.getTime().getTime();
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
}
