package io.excitinglab.lifemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class Alarm extends BroadcastReceiver {

    DatabaseHelper mDatabaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("Title");
        String list = intent.getStringExtra("List");
        int id = intent.getIntExtra("ID", -1);

        mDatabaseHelper = DatabaseHelper.getInstance(context);

        Intent resultIntent = new Intent(context, EditTaskActivity.class);
        resultIntent.putExtra("id", id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EditTaskActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_logo_temp_web_burned)
                        .setContentTitle(title)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentText(list);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean t = sharedPreferences.getBoolean("notifications_sound", true);

        if (t) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(uri);
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        }
        else {
            mBuilder.setDefaults(0);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());



        Task task = mDatabaseHelper.getTask(id);
        task.setReminder(0);
        mDatabaseHelper.updateTask(task);

    }

}

