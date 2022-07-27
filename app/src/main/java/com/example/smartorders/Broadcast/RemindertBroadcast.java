package com.example.smartorders.Broadcast;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartorders.R;

public class RemindertBroadcast extends BroadcastReceiver {
    private int nId =2;
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Set the alarm here.
//            AlarmManager alarmManager =
//                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pendingIntent =
//                    PendingIntent.getService(context, 2, intent,
//                            PendingIntent.FLAG_NO_CREATE);
//            if (pendingIntent != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//            }
//        }
        if (intent.getAction().equals("Reminder")) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyReservation")
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("Upcoming Reservation")
                    .setContentText(intent.getStringExtra("Message"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(nId++, builder.build());
        }

    }
}