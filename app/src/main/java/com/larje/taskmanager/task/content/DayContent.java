package com.larje.taskmanager.task.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DayContent {

    private Context context;
    private DBManager db;

    int textEnabledColor;
    int textDisabledColor;

    public DayContent(Context context){
        this.context = context;
        this.db = new DBManager(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout getContent(){

        switch (MainActivity.theme){
            case 0:
                textEnabledColor = context.getColor(R.color.darkThemeLevel2subtext);
                textDisabledColor = context.getColor(R.color.darkThemeLevel2text);
                break;
            case 1:
                textEnabledColor = context.getColor(R.color.darkThemeLevel2text);
                textDisabledColor = context.getColor(R.color.darkThemeLevel2subtext);
                break;
        }

        LinearLayout contentRoot = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,50);
        contentRoot.setLayoutParams(params);
        contentRoot.setOrientation(LinearLayout.VERTICAL);

        ArrayList dates = db.GetTaskDates();
        for (int i = 0; i < dates.size(); i++){
            contentRoot.addView(makeDayNote((ArrayMap) dates.get(i)));
        }
//        contentRoot.addView(makeAd());

        return contentRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    public LinearLayout makeDayNote(ArrayMap date){
        LinearLayout noteRoot = new LinearLayout(context);
        noteRoot.setOrientation(LinearLayout.VERTICAL);
        noteRoot.setElevation(5);
        noteRoot.setBackgroundDrawable(context.getDrawable(R.drawable.rounded_layout));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,20,20);
        noteRoot.setLayoutParams(params);

        noteRoot.addView(makeDate(date));
//        noteRoot.addView(makeTitleDivider());
        noteRoot.addView(makeTasks(date));

        return noteRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeDate(ArrayMap date){
        Calendar calendar = new GregorianCalendar();
        Calendar currentCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Calendar tomorrowCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1);
        Calendar dateCalendar = new GregorianCalendar(Integer.parseInt((String)date.get("year")), Integer.parseInt((String)date.get("month")), Integer.parseInt((String)date.get("day")));

        TextView noteDate = new TextView(context);
        noteDate.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (currentCalendar.getTimeInMillis() == dateCalendar.getTimeInMillis()){
            noteDate.setText(context.getString(R.string.today));
        } else if (tomorrowCalendar.getTimeInMillis() == dateCalendar.getTimeInMillis()){
            noteDate.setText(context.getString(R.string.tomorrow));
        }else {
            noteDate.setText(date.get("day")+"."+date.get("month")+"."+date.get("year"));
        }
        TypedValue textColor = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.titleTextColor, textColor, true);
        noteDate.setTextColor(textColor.data);
        noteDate.setBackgroundResource(R.drawable.rounded_top);
        noteDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
        noteDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noteDate.setTextSize(20);
        noteDate.setPadding(0,15, 0, 15);

        return noteDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout makeTasks(ArrayMap date){
        LinearLayout tasksRoot = new LinearLayout(context);
        tasksRoot.setOrientation(LinearLayout.VERTICAL);
        tasksRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tasksRoot.setBackgroundResource(R.drawable.rounded_bottom_level2);

        ArrayList tasks = db.GetTasksByDate(date.get("day")+"/"+date.get("month")+"/"+date.get("year"));
        if (tasks.size() == 0){
            tasksRoot.addView(makeNoTaskFiller());
        } else {
            for (int i = 0; i < tasks.size(); i++){
                tasksRoot.addView(makeOneTask((ArrayMap)tasks.get(i), date));
                if (i != tasks.size()-1){
                    tasksRoot.addView(makeDivider());
                }
            }
        }

        return tasksRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeNoTaskFiller(){
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText(context.getString(R.string.no_tasks));
        tv.setTextColor(context.getColor(R.color.darkThemeLevel2subtext));
        tv.setTextSize(25);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setPadding(0,15,0,15);
        return tv;
    }

    public View makeTitleDivider(){
        View d = new View(context);
        d.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        TypedValue dividerColor = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.tab2, dividerColor, true);
        d.setBackgroundColor(dividerColor.data);

        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View makeDivider(){
        View d = new View(context);
        d.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

        TypedValue dividerColor = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, dividerColor, true);

        d.setBackgroundColor(dividerColor.data);

        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout makeOneTask(ArrayMap task, ArrayMap date){
        ArrayMap element = db.GetElement((int)task.get("element"));
        int status = 0;
        if ((int)task.get("type") == 2){
            status = db.GetRepeatTask(date.get("day")+"/"+date.get("month")+"/"+date.get("year"), (int)task.get("id"));
        } else {
            status = (int)element.get("status");
        }
        LinearLayout taskRoot = new LinearLayout(context);
        taskRoot.setPadding(0,15, 0, 15);
        taskRoot.setOrientation(LinearLayout.HORIZONTAL);
        taskRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        taskRoot.addView(makeOneTaskTime((String)task.get("time"), status));
        taskRoot.addView(makeOneTaskName((String)element.get("name"), status));
        taskRoot.addView(makeOneTaskStatus(status, (int)element.get("id"), taskRoot, (int)task.get("type"), (int)task.get("id"), (date.get("day")+"/"+date.get("month")+"/"+date.get("year"))));

        return taskRoot;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeOneTaskTime(String time, int status){
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        tv.setTag("time");
        tv.setLayoutParams(params);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String hour = time.split("/")[0];
        String minute = time.split("/")[1];
        if (hour.length() < 2){
            hour = "0"+hour;
        }
        if (minute.length() < 2){
            minute = "0"+minute;
        }
        tv.setText(hour+":"+minute);
        tv.setTextSize(22);
        if (status == 0){
            tv.setTextColor(textEnabledColor);
        } else {
            tv.setTextColor(textDisabledColor);
        }
        tv.setPadding(10, 10,10,10);

        return tv;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeOneTaskName(String name, int status){
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 3;
        tv.setTag("name");
        tv.setLayoutParams(params);
        tv.setText(name);
        tv.setTextSize(22);
        if (status == 0){
            tv.setTextColor(textEnabledColor);
        } else {
            tv.setTextColor(textDisabledColor);
        }
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setPadding(10, 10,10,10);
        return tv;
    }

    public LinearLayout makeOneTaskStatus(final int status, final int elementId, final LinearLayout taskRoot, final int type, final int taskId, final String date){
        LinearLayout btnRoot = new LinearLayout(context);
        LinearLayout.LayoutParams rootparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        btnRoot.setLayoutParams(rootparams);
        rootparams.weight = 1;

        RelativeLayout btnLay = new RelativeLayout(context);
        RelativeLayout.LayoutParams layParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnLay.setLayoutParams(layParams);
        btnLay.setPadding(0, 0, 20, 0);

        ToggleButton btn = new ToggleButton(context);
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        btn.setLayoutParams(params);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTextOff("");
        btn.setTextOn("");
        if (status == 0){
            btn.setBackgroundDrawable(context.getDrawable(R.drawable.ic_nodone));
            btn.setChecked(false);
        } else {
            btn.setBackgroundDrawable(context.getDrawable(R.drawable.ic_done));
            btn.setChecked(true);
        }
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (type == 2){
                        db.UpdateRepeatTask(date, taskId, 1);
                    } else {
                        db.UpdateStatus(elementId, 1);
                    }
                    buttonView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_done));
                    ((TextView)taskRoot.findViewWithTag("time")).setTextColor(textDisabledColor);
                    ((TextView)taskRoot.findViewWithTag("name")).setTextColor(textDisabledColor);
                } else {
                    if (type == 2){
                        db.UpdateRepeatTask(date, taskId, 0);
                    } else {
                        db.UpdateStatus(elementId, 0);
                    }
                    buttonView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_nodone));
                    ((TextView)taskRoot.findViewWithTag("time")).setTextColor(textEnabledColor);
                    ((TextView)taskRoot.findViewWithTag("name")).setTextColor(textEnabledColor);
                }
            }
        });
        btnLay.addView(btn);
        btnRoot.addView(btnLay);
        return btnRoot;
    }

}
