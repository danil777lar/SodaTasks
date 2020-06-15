package com.larje.taskmanager.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.SplashActivity;
import com.larje.taskmanager.data.DBManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class TodayTaskWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds){
            updateWidget(context, appWidgetManager, i);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int id){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_today_task);

        makeUpdateBtn(context, remoteViews, id);
        makeOpenBtn(context, remoteViews, id);
        makeTaskList(context, remoteViews, id);
        makeTaskClick(context, remoteViews, id);

        appWidgetManager.updateAppWidget(id, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.widget_todaytask_conteiner);
    }

    private void makeUpdateBtn(Context context, RemoteViews remoteViews, int id){
        Intent intent = new Intent(context, TodayTaskWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
        PendingIntent pIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_todaytask_updatebtn, pIntent);
    }

    private void makeOpenBtn(Context context, RemoteViews remoteViews, int id){
        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, id, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_todaytask_openbtn, pIntent);
    }

    private void makeTaskList(Context context, RemoteViews remoteViews, int id){
        Intent adapter = new Intent(context, TodayTaskService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        remoteViews.setRemoteAdapter(R.id.widget_todaytask_conteiner, adapter);
    }

    private void makeTaskClick(Context context, RemoteViews remoteViews, int id){
        Intent clickIntent = new Intent(context, TodayTaskWidget.class);
        clickIntent.setAction("actionOnClick");
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.widget_todaytask_conteiner, pIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if ((intent.getAction().equals("actionOnClick")) & (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID)){
            int elementId = intent.getIntExtra("listElementId", -1);
            if (elementId != -1){
                updateTaskStatus(context, elementId);
                updateWidget(context, AppWidgetManager.getInstance(context), widgetId);
            }
        }
    }

    private void updateTaskStatus(Context context, int elementId){
        DBManager db = new DBManager(context);
        Calendar calendar = new GregorianCalendar();

        int status = -1;
        ArrayMap task = db.GetTask(elementId);
        ArrayMap element = db.GetElement(elementId);
        String sDate = calendar.get(Calendar.DAY_OF_MONTH)+"/"
                      +calendar.get(Calendar.MONTH)+"/"
                      +calendar.get(Calendar.YEAR);

        switch ((int)task.get("type")){
            case 0:
                status = (int)element.get("status");
                switch (status){
                    case 0:
                        db.UpdateStatus(elementId, 1);
                        break;
                    case 1:
                        db.UpdateStatus(elementId, 0);
                        break;
                }
                break;
            case 2:
                status = db.GetRepeatTask(sDate, (int)task.get("id"));
                switch (status){
                    case 0:
                        db.UpdateRepeatTask(sDate, (int)task.get("id"), 1);
                        break;
                    case 1:
                        db.UpdateRepeatTask(sDate, (int)task.get("id"), 0);
                        break;
                }
                break;
        }

//        Toast.makeText(context, (String)element.get("name"), Toast.LENGTH_SHORT).show();
//        return "lox";
    }
}
