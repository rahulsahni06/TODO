package com.sahni.rahul.todo.broadcastReceivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sahni.rahul.todo.helpers.IntentConstants;
import com.sahni.rahul.todo.R;
import com.sahni.rahul.todo.activity.MainActivity;

public class MyTodoReceiver extends BroadcastReceiver {

    static int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra(IntentConstants.TODO_TITLE);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, resultIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_watch_later_black_24dp)
                .setContentTitle("Aren't you forgetting something?")
                .setContentText(title)
                .setAutoCancel(true);


        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(i++, builder.build());

    }
}
