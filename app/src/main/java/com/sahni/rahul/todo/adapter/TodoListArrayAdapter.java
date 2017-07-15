package com.sahni.rahul.todo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.helpers.EpochToDateTime;
import com.sahni.rahul.todo.interfaces.CheckBoxClickedListener;
import com.sahni.rahul.todo.models.TodoClass;

import java.util.ArrayList;

import static com.sahni.rahul.todo.database.DatabaseConstants.TIME_NOT_SET;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_DONE;

/**
 * Created by sahni on 24-Jun-17.
 */

public class TodoListArrayAdapter extends ArrayAdapter<TodoClass> {

    ArrayList<TodoClass> arrayList;
    Context context;
    int oldColor;

    CheckBoxClickedListener listener;

    public void setCheckBoxClickedListener(CheckBoxClickedListener listener){
        this.listener = listener;
    }


    public TodoListArrayAdapter(@NonNull Context context, ArrayList<TodoClass> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

//        View v;

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, null);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.date_text_view);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.time_text_view);
            TextView categoryTextView = (TextView) convertView.findViewById(R.id.category_text_view);
            CheckBox statusCheckBox = (CheckBox) convertView.findViewById(R.id.status_check_box);
            oldColor = categoryTextView.getCurrentTextColor();

            convertView.setTag(new TodoViewHolder(titleTextView, dateTextView, timeTextView, categoryTextView, statusCheckBox));
        }

        TodoClass todoClass = arrayList.get(position);
        TodoViewHolder todoViewHolder = (TodoViewHolder) convertView.getTag();
        todoViewHolder.titleTextView.setText(todoClass.getTitle());
        int status = todoClass.getStatus();
        if(status == TODO_DONE){
            todoViewHolder.statusCheckBox.setChecked(true);
        }
        else{
            todoViewHolder.statusCheckBox.setChecked(false);
        }

//        Calendar calendar = Calendar.getInstance();
        long date = todoClass.getDate();
        long time = todoClass.getTime();

        if(EpochToDateTime.convert(date).equalsIgnoreCase("Today") &&  (time > System.currentTimeMillis() || time == TIME_NOT_SET)){
            todoViewHolder.dateTextView.setTextColor(ContextCompat.getColor(context, R.color.todoColorPrimary));
            todoViewHolder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.todoColorPrimary));
        }
        else if((time != TIME_NOT_SET && time < System.currentTimeMillis())
                || (time == TIME_NOT_SET && date < System.currentTimeMillis())){
            todoViewHolder.dateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
            todoViewHolder.timeTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        }

        else{
            todoViewHolder.dateTextView.setTextColor(oldColor);
            todoViewHolder.timeTextView.setTextColor(oldColor);
        }

        todoViewHolder.dateTextView.setText(EpochToDateTime.convert(todoClass.getDate()));
        todoViewHolder.timeTextView.setText(EpochToDateTime.convertTime(todoClass.getTime()));
        todoViewHolder.categoryTextView.setText(todoClass.getCategory());

        todoViewHolder.statusCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null){

                    CheckBox checkBox = (CheckBox) v;
                    listener.checkBoxClicked(checkBox, position);
                }
            }
        });
//        todoViewHolder.statusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if(buttonView.)
//                if(listener != null){
//                    listener.checkBoxClicked(buttonView, position, isChecked);
//                }
//            }
//        });

        return convertView;


    }

    public static class TodoViewHolder{
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView categoryTextView;
        CheckBox statusCheckBox;

        public TodoViewHolder(TextView titleTextView , TextView dateTextView, TextView timeTextView, TextView categoryTextView,
                              CheckBox statusCheckBox){
            this.titleTextView = titleTextView;
            this.dateTextView = dateTextView;
            this.timeTextView = timeTextView;
            this.categoryTextView = categoryTextView;
            this.statusCheckBox = statusCheckBox;
        }
    }


}

