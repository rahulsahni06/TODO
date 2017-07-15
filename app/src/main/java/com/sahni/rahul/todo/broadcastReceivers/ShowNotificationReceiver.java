package com.sahni.rahul.todo.broadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.activity.MainActivity;
import com.sahni.rahul.todo.database.DatabaseConstants;
import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.IntentConstants;

public class ShowNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(context).getWritableDatabase();


        String title = intent.getStringExtra(IntentConstants.TODO_TITLE);
        int pendingIntentId = intent.getIntExtra(IntentConstants.TODO_PENDING_ID, 0);

        ContentValues cv = new ContentValues();
        cv.put(TodoOpenHelper.TODO_ALARM_STATUS, DatabaseConstants.ALARM_OVER);

        database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_PENDING_INTENT_ID + " = "+pendingIntentId, null);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, resultIntent, 0);

        Intent markAsFinishedIntent = new Intent(context, NotificationActionReceiver.class);
        markAsFinishedIntent.putExtra(IntentConstants.TODO_PENDING_ID, pendingIntentId);
        markAsFinishedIntent.setAction(IntentConstants.MARK_TODO_AS_DONE_ACTION);
        PendingIntent finishedPendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, markAsFinishedIntent, 0 );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_watch_later_black_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("Aren't you forgetting something?")
                .setContentText(title)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(0, "Mark as done", finishedPendingIntent );

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(pendingIntentId, builder.build());

    }
}
