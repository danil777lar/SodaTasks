package com.larje.taskmanager.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

public class NotificationReciever extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationAlarm notificationAlarm = new NotificationAlarm(context);
        DBManager db = new DBManager(context);
        ArrayMap settings = db.GetSettings();
        if ((intent.getBooleanExtra("isNotification", false)) & ((int)settings.get("notswitch") == 1)){
            sendNotification(context, intent.getIntExtra("taskId", 0));
        }
        notificationAlarm.restart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(Context context, int taskId){
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        DBManager db = new DBManager(context);
        ArrayMap task = db.GetTask(taskId);
        ArrayMap taskElement = db.GetElement(taskId);

        String[] time = task.get("time").toString().split("/");
        for (int i = 0; i < 2; i++){
            if (time[i].length() < 2){
                time[i] = "0"+time[i];
            }
        }

        Intent notyIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String CHANNEL_ID = "com.larje.taskmanager.notify";
        String CHANNEL_NAME = "myChannel";
        int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE);
        nm.createNotificationChannel(mChannel);



        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_task)
                        .setContentTitle(time[0]+":"+time[1])
                        .setContentText(taskElement.get("name").toString())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(contentIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)taskElement.get("id"), notificationBuilder.build());
    }

}
