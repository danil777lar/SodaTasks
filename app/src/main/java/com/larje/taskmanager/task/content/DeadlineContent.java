package com.larje.taskmanager.task.content;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.larje.taskmanager.customview.RoundedBackground;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;

public class DeadlineContent {

    private Context context;
    private DBManager db;

    public DeadlineContent(Context context){
        this.context = context;
        this.db = new DBManager(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout getContent(){
        LinearLayout contentRoot = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,50);
        contentRoot.setLayoutParams(params);
        contentRoot.setOrientation(LinearLayout.VERTICAL);

        contentRoot.addView(makeDeadlineNote());

        return contentRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private LinearLayout makeDeadlineNote(){
        LinearLayout noteRoot = new LinearLayout(context);
        noteRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,40,20,0);
        noteRoot.setLayoutParams(params);

        noteRoot.addView(makeTitle());
        noteRoot.addView(makeTasks());

        return noteRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View makeTitle(){
        View back = new View(context);
        back.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));

        TypedValue titleBackground = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, titleBackground, true);

        RoundedBackground background = new RoundedBackground(context, titleBackground.data, true,  true, true, true);
        back.setBackground(background);

        return back;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout makeTasks(){
        LinearLayout tasksRoot = new LinearLayout(context);
        tasksRoot.setOrientation(LinearLayout.VERTICAL);
        tasksRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tasksRoot.setBackgroundResource(R.drawable.rounded_bottom_level2);

        ArrayList tasks = db.GetDeadlines();
        if (tasks.size() == 0){
            tasksRoot.addView(makeNoTaskFiller());
        } else {
            for (int i = 0; i < tasks.size(); i++){
                tasksRoot.addView(makeOneTask((ArrayMap)tasks.get(i)));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View makeDivider(){
        View d = new View(context);
        d.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        d.setBackgroundColor(context.getColor(R.color.darkThemeLevel1));

        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout makeOneTask(ArrayMap task){
        ArrayMap element = db.GetElement((int)task.get("element"));

        LinearLayout taskRoot = new LinearLayout(context);
        taskRoot.setPadding(0,15, 0, 15);
        taskRoot.setOrientation(LinearLayout.HORIZONTAL);
        taskRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        int status = (int)(db.GetElement((int)task.get("element"))).get("status");
        taskRoot.addView(makeOneTaskTime((String)task.get("time"), (String)task.get("date")));
        taskRoot.addView(makeOneTaskName((String)element.get("name")));
        taskRoot.addView(makeOneTaskStatus(status, (int)element.get("id"), taskRoot, (int)task.get("type"), (int)task.get("id"), (String)task.get("date")));

        return taskRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeOneTaskName(String name){
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 6;
        tv.setTag("name");
        tv.setLayoutParams(params);
        tv.setText(name);
        tv.setTypeface(MainActivity.face);
        tv.setTextSize(22);
        tv.setTextColor(context.getColor(R.color.darkThemeLevel2text));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setPadding(10, 10,10,10);
        return tv;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TextView makeOneTaskTime(String time, String date){
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 3;
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(5,0,0,0);

        String[] sDateArray = date.split("/");
        String[] sTimeArray = time.split("/");

        if (sDateArray[0].length() < 2){
            sDateArray[0] = "0"+sDateArray[0];
        }
        if (sDateArray[1].length() < 2){
            sDateArray[1] = "0"+sDateArray[1];
        }
        if (sTimeArray[0].length() < 2){
            sTimeArray[0] = "0"+sTimeArray[0];
        }
        if (sTimeArray[1].length() < 2){
            sTimeArray[1] = "0"+sTimeArray[1];
        }
        String sDate = sDateArray[0]+"."+sDateArray[1]+"."+sDateArray[2];
        String sTime = sTimeArray[0]+":"+sTimeArray[1];


        tv.setTextColor(context.getColor(R.color.darkThemeLevel2text));
        tv.setText(sDate+"\n"+context.getString(R.string.to_horizontal)+"     "+sTime);
        tv.setTextSize(19);
        tv.setTag("date");

//        LinearLayout timeRoot = new LinearLayout(context);
//        timeRoot.setOrientation(LinearLayout.HORIZONTAL);
//        timeRoot.setGravity(Gravity.CENTER);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 2;
//        params.gravity = Gravity.CENTER;
//        timeRoot.setLayoutParams(params);


//        TextView typeView = new TextView(context);
//        typeView.setTag("typeView");
//        typeView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        typeView.setTextColor(context.getColor(R.color.darkThemeLevel2text));
//        typeView.setTextSize(20);
//        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 1;
//        typeView.setLayoutParams(params);
//        timeRoot.addView(typeView);
//        if (deadline){
//            typeView.setText("д\nо");
//            params.weight = 1;
//        }
//
//        TextView dateView = new TextView(context);
//        dateView.setTag("dateView");
//        dateView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        dateView.setTextColor(context.getColor(R.color.darkThemeLevel2text));
//        dateView.setTextSize(19);
//        String[] dateArray = date.split("/");
//        for (int i = 0; i < 2; i++){
//            if (dateArray[i].length() < 2){
//                dateArray[i] = "0"+dateArray[i];
//            }
//        }
//        dateView.setText(dateArray[0]+"."+dateArray[1]+"\n"+dateArray[2]);
//        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 2;
//        dateView.setLayoutParams(params);
//        timeRoot.addView(dateView);

//        tv.setTag("time");
//        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        String hour = time.split("/")[0];
//        String minute = time.split("/")[1];
//        if (hour.length() < 2){
//            hour = "0"+hour;
//        }
//        if (minute.length() < 2){
//            minute = "0"+minute;
//        }
//        tv.setText(hour+":"+minute);
//        tv.setTextSize(24);
//        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 2;
//        tv.setLayoutParams(params);
//        tv.setTextColor(context.getColor(R.color.darkThemeLevel2text));

//        timeRoot.addView(tv);

//        View divider = new View(context);
//        divider.setBackgroundColor(context.getColor(R.color.darkThemeLevel1));
//        params = new LinearLayout.LayoutParams(3, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.setMargins(20,0,0,4);
//        divider.setLayoutParams(params);
//        timeRoot.addView(divider);
        return tv;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout makeOneTaskStatus(final int status, final int elementId, final LinearLayout taskRoot, final int type, final int taskId, final String date){
        LinearLayout btnRoot = new LinearLayout(context);
        LinearLayout.LayoutParams rootparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        btnRoot.setLayoutParams(rootparams);
        rootparams.weight = 2;

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
            ((TextView)taskRoot.findViewWithTag("date")).setTextColor(context.getColor(R.color.darkThemeLevel2text));
            ((TextView)taskRoot.findViewWithTag("name")).setTextColor(context.getColor(R.color.darkThemeLevel2text));
            btn.setChecked(false);
        } else {
            btn.setBackgroundDrawable(context.getDrawable(R.drawable.ic_done));
            ((TextView)taskRoot.findViewWithTag("date")).setTextColor(context.getColor(R.color.darkThemeLevel2subtext));
            ((TextView)taskRoot.findViewWithTag("name")).setTextColor(context.getColor(R.color.darkThemeLevel2subtext));
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
                    ((TextView)taskRoot.findViewWithTag("date")).setTextColor(context.getColor(R.color.darkThemeLevel2subtext));
                    ((TextView)taskRoot.findViewWithTag("name")).setTextColor(context.getColor(R.color.darkThemeLevel2subtext));
                } else {
                    if (type == 2){
                        db.UpdateRepeatTask(date, taskId, 0);
                    } else {
                        db.UpdateStatus(elementId, 0);
                    }
                    buttonView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_nodone));
                    ((TextView)taskRoot.findViewWithTag("date")).setTextColor(context.getColor(R.color.darkThemeLevel2text));
                    ((TextView)taskRoot.findViewWithTag("name")).setTextColor(context.getColor(R.color.darkThemeLevel2text));
                }
            }
        });
        btnLay.addView(btn);
        btnRoot.addView(btnLay);
        return btnRoot;
    }

}
