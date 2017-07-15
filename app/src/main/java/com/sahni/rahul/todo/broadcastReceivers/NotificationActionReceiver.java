package com.sahni.rahul.todo.broadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.sahni.rahul.todo.database.TodoOpenHelper;
import com.sahni.rahul.todo.helpers.IntentConstants;

import static com.sahni.rahul.todo.database.DatabaseConstants.ALARM_OVER;
import static com.sahni.rahul.todo.database.DatabaseConstants.TODO_DONE;

public class NotificationActionReceiver extends BroadcastReceiver{



    @Override
    public void onReceive(Context context, Intent intent) {
        int pendingId = intent.getIntExtra(IntentConstants.TODO_PENDING_ID, 0);
        Log.i("Notification", "id = "+pendingId);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pendingId);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        String action = intent.getAction();

        if(action.equals(IntentConstants.MARK_TODO_AS_DONE_ACTION)){
            SQLiteDatabase database = TodoOpenHelper.getOpenHelperInstance(context).getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(TodoOpenHelper.TODO_STATUS, TODO_DONE);
            cv.put(TodoOpenHelper.TODO_ALARM_STATUS, ALARM_OVER);
            database.update(TodoOpenHelper.TODO_TABLE, cv, TodoOpenHelper.TODO_PENDING_INTENT_ID + " = "+ pendingId, null);

            Toast.makeText(context, "Todo done", Toast.LENGTH_SHORT).show();

            Intent i = new Intent();
                    i.setAction(IntentConstants.MARK_TODO_AS_DONE_ACTION);
            context.sendBroadcast(i);

        }


    }


}
