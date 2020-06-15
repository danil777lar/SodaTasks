package com.larje.taskmanager.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.RequiresApi;

import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TodayTaskItemAdapter implements RemoteViewsService.RemoteViewsFactory {

    ArrayList<Integer> elementId;
    ArrayList<String> time;
    ArrayList<String> name;
    ArrayList<Integer> status;

    Context context;
    int widgetId;
    DBManager db;

    TodayTaskItemAdapter(Context context, Intent intent){
        this.context = context;
        this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.db = new DBManager(context);
    }

    @Override
    public void onCreate() {
        elementId = new ArrayList<Integer>();
        time = new ArrayList<String>();
        name = new ArrayList<String>();
        status = new ArrayList<Integer>();
    }

    @Override
    public void onDataSetChanged() {
        elementId.clear();
        time.clear();
        name.clear();
        status.clear();
        Calendar currentDate = new GregorianCalendar();
        String sDate = currentDate.get(Calendar.DAY_OF_MONTH)+"/"
                             +currentDate.get(Calendar.MONTH)+"/"
                             +currentDate.get(Calendar.YEAR);
        ArrayList data = db.GetTasksByDate(sDate);
        for (int i = 0; i < data.size(); i++){
            ArrayMap task = (ArrayMap)data.get(i);
            ArrayMap element = (ArrayMap) db.GetElement((int)task.get("element"));
            elementId.add((int)element.get("id"));
            time.add((String)task.get("time"));
            name.add((String)element.get("name"));

            switch ((int)task.get("type")){
                case 0:
                    status.add((int)element.get("status"));
                    break;
                case 2:
                    status.add(db.GetRepeatTask(sDate, (int)task.get("id")));
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.task_day_list_item);
        switch (status.get(position)){
            case 0:
                view.setImageViewResource(R.id.widget_todaytask_item_checkbtn, R.drawable.ic_nodone);
                view.setTextColor(R.id.widget_todaytask_item_name, Color.parseColor("#666666"));
                break;
            case 1:
                view.setImageViewResource(R.id.widget_todaytask_item_checkbtn, R.drawable.ic_done);
                view.setTextColor(R.id.widget_todaytask_item_name, Color.parseColor("#888888"));
                break;
        }
        String sHour = time.get(position).split("/")[0];
        String sMinute = time.get(position).split("/")[1];
        if (sHour.length() < 2){sHour = "0"+sHour;}
        if (sMinute.length() < 2){sMinute = "0"+sMinute;}
        view.setTextViewText(R.id.widget_todaytask_item_time, sHour+":"+sMinute);
        view.setTextViewText(R.id.widget_todaytask_item_name, name.get(position));

        Intent clickIntent = new Intent();
        clickIntent.putExtra("listElementId", elementId.get(position));
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        view.setOnClickFillInIntent(R.id.widget_todaytask_item_checkbtn, clickIntent);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
