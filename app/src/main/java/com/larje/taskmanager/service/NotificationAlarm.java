package com.larje.taskmanager.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;

import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationAlarm {

    private Context context;

    public NotificationAlarm(Context context){
        this.context = context;
    }

    public void restart(){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        long time = getTime(intent);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pIntent);
        am.set(AlarmManager.RTC_WAKEUP, time, pIntent);
        Calendar t = new GregorianCalendar();
        t.setTimeInMillis(time);
        Log.d("nrecieve", t.getTime().toString());
    }

    private long getTime(Intent intent){
        DBManager db = new DBManager(context);
        ArrayMap settings = db.GetSettings();
        Calendar calendar = new GregorianCalendar();
        int timeHour = calendar.get(Calendar.HOUR_OF_DAY);
        int timeMinute = calendar.get(Calendar.MINUTE);
        ArrayList tasks = db.GetTasksByDate(calendar.get(Calendar.DAY_OF_MONTH)+"/"
                                           +calendar.get(Calendar.MONTH)+"/"
                                           +calendar.get(Calendar.YEAR));
        ArrayMap finalTask = new ArrayMap();
        for (int i = 0; i < tasks.size(); i++){
            ArrayMap iTask = (ArrayMap)tasks.get(i);
            String[] iTime = iTask.get("time").toString().split("/");
            if (Integer.parseInt(iTime[0]) > timeHour){
                finalTask = iTask;
                break;
            } else if(Integer.parseInt(iTime[0]) == timeHour){
                if (Integer.parseInt(iTime[1]) >= timeMinute){
                    finalTask = iTask;
                    break;
                }
            }
        }

        if (!finalTask.isEmpty()){
            String[] sTime = ((String)finalTask.get("time")).split("/");
            Log.d("tdate", ""+finalTask.get("date"));
            int parHour = Integer.parseInt(sTime[0]);
            int parMinute = Integer.parseInt(sTime[1]) - (int)settings.get("nottime");
            if (parMinute < 0){
                parMinute = 60 + parMinute;
                parHour -= 1;
            }
            calendar.set(Calendar.HOUR_OF_DAY, parHour);
            calendar.set(Calendar.MINUTE, parMinute);
            calendar.set(Calendar.SECOND, 59);
            intent.putExtra("taskId", (int)finalTask.get("element"));
            intent.putExtra("isNotification", true);
            return calendar.getTimeInMillis();
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 1);
        intent.putExtra("isNotification", false);
        return calendar.getTimeInMillis();
    }

}
