package com.example.smartorders.Broadcast;

import static com.example.smartorders.ForeGround.App.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;

import com.example.smartorders.Activities.HomePageActivity;
import com.example.smartorders.Classes.Order;
import com.example.smartorders.R;

import java.util.List;

public class BroadCastRec extends BroadcastReceiver {
    LiveData<List<Order>> orderList;

    public BroadCastRec(LiveData<List<Order>> orderList) {
        this.orderList = orderList;
    }
    public BroadCastRec(){}
    @Override
    public void onReceive(Context context, Intent intent) {
        // listen to "Broad" broadcast event
        if (intent.getAction().equals("Broad")) {
            // get values from intent extra
            Intent notificationIntent = new Intent(context, HomePageActivity.class);
            String numberOfAttendees = intent.getStringExtra("number");
            String dateOfReservation = intent.getStringExtra("date");
            try { // create the notification and notify to user
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setContentTitle("You have an upcoming  reservation soon!")
//                        .setContentText("Date:" + dateOfReservation + "    Number of attendees:" + numberOfAttendees)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .setSmallIcon(R.drawable.delete_item).build();
//                NotificationManagerCompat.from(context).notify(1, notification);


                //Intent intent=new Intent();
                String CHANNEL_ID="MYCHANNEL";
                NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
                PendingIntent pendingIntent=PendingIntent.getActivity(context,1,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
                Notification notification=new Notification.Builder(context,CHANNEL_ID)
                        .setContentTitle("You have an upcoming  reservation soon!")
                        .setContentText("Date:" + dateOfReservation + "    Number of attendees:" + numberOfAttendees)
                        .setContentIntent(pendingIntent)
                        .addAction(android.R.drawable.sym_action_chat,"Title",pendingIntent)
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_action_chat)
                        .build();

                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.notify(1,notification);
            } catch (Exception e) {
                Log.e("BroadCast", "Error on notification: " + e.getMessage());
            }
        }
    }

}
