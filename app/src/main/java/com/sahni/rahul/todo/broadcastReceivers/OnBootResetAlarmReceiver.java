package com.sahni.rahul.todo.broadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.IntentConstants;

public class OnBootResetAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(context).getReadableDatabase();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int count = 0;
        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE, null, TodoOpenHelper.TODO_ALARM_STATUS+ " = 1", null, null, null, null);
        while (cursor.moveToNext()) {
            count++;

            String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TASK));
            long timeInEpoch = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
            int pendingIntentId = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PENDING_INTENT_ID));


            Intent showNotificationIntent = new Intent(context, ShowNotificationReceiver.class);
            showNotificationIntent.putExtra(IntentConstants.TODO_TITLE, title);
            showNotificationIntent.putExtra(IntentConstants.TODO_PENDING_ID, pendingIntentId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, showNotificationIntent, 0);
            alarmManager.set(AlarmManager.RTC, timeInEpoch, pendingIntent);


        }

        Log.i("MyAppBootReceiver", "count = "+count);
        cursor.close();

    }
}
