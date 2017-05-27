package io.excitinglab.xtasker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Alarm extends BroadcastReceiver {

    DatabaseHelper mDatabaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("HEY: ", "IT WORKED!!!");

        mDatabaseHelper = mDatabaseHelper.getInstance(context);

        int currentTime = (int) System.currentTimeMillis() / 1000;

        int ID = 0;

        int reminderTime;
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(mDatabaseHelper.getAllTasks());
        for (int i = 0; i < tasks.size(); i++) {
            reminderTime = (int) tasks.get(i).getReminder() / 1000;
            if (currentTime == reminderTime) {
                ID = (int) tasks.get(i).getId();
            }
        }



//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_alarm_black_18dp)
//                .setContentTitle("My first alarm")
//                .setContentText("some text...");
//        Intent intentToFire = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToFire, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());



        String list;
        if (mDatabaseHelper.getTask(ID).getP_id() == 0) list = "Inbox";
        else list = mDatabaseHelper.getListByID(mDatabaseHelper.getTask(ID).getP_id()).getName();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_alarm_black_18dp)
                        .setContentTitle(mDatabaseHelper.getTask(ID).getName())
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

        Intent resultIntent = new Intent(context, EditTaskActivity.class);
        resultIntent.putExtra("id", ID);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EditTaskActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());




//        Calendar now = GregorianCalendar.getInstance();
//        int dayOfWeek = now.get(Calendar.DATE);
//        if(dayOfWeek != 1 && dayOfWeek != 7) {
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(context)
//                            .setSmallIcon(R.drawable.ic_alarm_black_18dp)
//                            .setContentTitle("Title")
//                            .setContentText("Subtitle");
//            Intent resultIntent = new Intent(context, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addParentStack(MainActivity.class);
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(1, mBuilder.build());
//        }


//        Intent intent1 = new Intent(context, MyNewIntentService.class);
//        context.startService(intent1);


//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();
//
//        // Put here YOUR code.
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
//
//        wl.release();




//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.ic_alarm_black_18dp)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//        Intent resultIntent = new Intent(context, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }



    public void setAlarm(Context context)
    {
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}

