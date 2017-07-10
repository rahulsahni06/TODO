package com.sahni.rahul.todo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.EpochToDate;
import com.sahni.rahul.todo.models.TodoClass;

import java.util.ArrayList;

/**
 * Created by sahni on 09-Jul-17.
 */

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.TodoViewHolder> {

    private int oldColor;
    ArrayList<TodoClass> arrayList;
    Context context;

    public TodoRecyclerAdapter(Context context, ArrayList<TodoClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, null);

        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        TodoClass todo = arrayList.get(position);


        holder.titleTextView.setText(todo.getTitle());


        int status = todo.getStatus();
        if(status == TodoOpenHelper.DONE){
            holder.statusCheckBox.setChecked(true);
        }
        else{
            holder.statusCheckBox.setChecked(false);
        }

//        Calendar calendar = Calendar.getInstance();
        long date = todo.getDate();
        long time = todo.getTime();

        if(EpochToDate.convert(date).equalsIgnoreCase("Today") &&  (time > System.currentTimeMillis() || time == EpochToDate.NO_TIME_SELECTED)){
            holder.dateTextView.setTextColor(ContextCompat.getColor(context, R.color.todoColorPrimary));
            holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.todoColorPrimary));
        }
        else if((time != EpochToDate.NO_TIME_SELECTED && time < System.currentTimeMillis())
                || (time == EpochToDate.NO_TIME_SELECTED && date < System.currentTimeMillis())){
            holder.dateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
            holder.timeTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        }

        else{
            holder.dateTextView.setTextColor(oldColor);
            holder.timeTextView.setTextColor(oldColor);
        }

        holder.dateTextView.setText(EpochToDate.convert(todo.getDate()));
        holder.timeTextView.setText(EpochToDate.convertTime(todo.getTime()));
        holder.categoryTextView.setText(todo.getCategory());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class TodoViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView categoryTextView;
        CheckBox statusCheckBox;

        public TodoViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            timeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            categoryTextView = (TextView) itemView.findViewById(R.id.category_text_view);
            statusCheckBox = (CheckBox) itemView.findViewById(R.id.status_check_box);
            oldColor = categoryTextView.getCurrentTextColor();
        }
    }
}
