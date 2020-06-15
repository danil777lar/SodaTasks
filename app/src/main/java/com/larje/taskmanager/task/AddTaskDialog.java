package com.larje.taskmanager.task;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddTaskDialog extends DialogFragment implements DialogInterface.OnClickListener {

    int elementId;
    View parrentView;

    boolean calendarOpen = false;

    boolean deadline = false;
    boolean repeat = false;
    int hour = 10;
    int minute = 28;

    int year = 2020;
    int month = 2;
    int day = 1;
    ArrayList weekDaysStates = new ArrayList();
    String weekDays[];

    int textColor;
    int textColorSub;
    int textColor2;
    int textColorSub2;

    public AddTaskDialog(int elementId, View parrentView){
        this.elementId = elementId;
        this.parrentView = parrentView;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        View v = inflater.inflate(R.layout.add_task_dialog, null);
        this.weekDays = new String[]{getContext().getString(R.string.mon), getContext().getString(R.string.tue), getContext().getString(R.string.wed), getContext().getString(R.string.thu), getContext().getString(R.string.fri), getContext().getString(R.string.sat), getContext().getString(R.string.sun)};
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        v.findViewWithTag("root").setBackgroundResource(R.drawable.rounded_layout);

        TypedValue textColorValue = new TypedValue();
        TypedValue textColorSubValue = new TypedValue();
        TypedValue textColorValue2 = new TypedValue();
        TypedValue textColorSubValue2 = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.dialogText, textColorValue, true);
        getContext().getTheme().resolveAttribute(R.attr.dialogTextSub, textColorSubValue, true);
        getContext().getTheme().resolveAttribute(R.attr.dialogText2, textColorValue2, true);
        getContext().getTheme().resolveAttribute(R.attr.dialogTextSub2, textColorSubValue2, true);
        textColor2 = textColorValue2.data;
        textColorSub2 = textColorSubValue2.data;

        getData(v);
        makeTypeButton(v);
        makeTimeView(v);
        makeDateButton(v);
        makeCalendarOkButton(v);
        makeWeekDaysButtons(v);
        makeCalendar(v);
        makeCancelButton(v);
        makeOkButton(v);
        makeDeleteButton(v);

        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makeTypeButton(View v){
        final Button inBtn = v.findViewById(R.id.add_task_dialog_type_in);
        final Button toBtn = v.findViewById(R.id.add_task_dialog_type_to);
        inBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (deadline == true){
                    deadline = false;
                    ((Button)v).setTextColor(textColor2);
                    toBtn.setTextColor(textColorSub2);
                }
            }
        });
        toBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (deadline == false){
                    deadline = true;
                    ((Button)v).setTextColor(textColor2);
                    inBtn.setTextColor(textColorSub2);
                }
            }
        });
        if (repeat){
            v.findViewById(R.id.task_type_button_panel).setVisibility(View.GONE);
        }
        if (deadline){
            toBtn.setTextColor(textColor2);
            inBtn.setTextColor(textColorSub2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makeTimeView(View v){
        ScrollView hour_scroll = v.findViewById(R.id.hour_scroll);
        LinearLayout hour_container = v.findViewById(R.id.hour_container);
        fillTimeContainer(hour_container, 24);
        setStartScroll(hour_scroll, hour_container, 24);
        setTimeChangeListener(hour_scroll, hour_container, 24);

        ScrollView minute_scroll = v.findViewById(R.id.minute_scroll);
        LinearLayout minute_container = v.findViewById(R.id.minute_container);
        fillTimeContainer(minute_container, 60);
        setStartScroll(minute_scroll, minute_container, 60);
        setTimeChangeListener(minute_scroll, minute_container, 60);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    public void fillTimeContainer(LinearLayout container, int time){
        for (int i = 0; i < time+2; i++){
            TextView txt = new TextView(getContext());
            txt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            txt.setTextSize(25);
            if (time == 24){
                txt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }else{
                txt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }
            txt.setTextColor(textColorSub2);
            if ((i == 0) | (i == time+1)){
                txt.setText("");
            }else{
                txt.setText(""+(i-1));
                if (txt.getText().toString().length() < 2){txt.setText("0"+txt.getText());}
            }
            txt.setTag(""+i);
            txt.setGravity(Gravity.CENTER);
            container.addView(txt);
        }
    }

    public void setStartScroll(final ScrollView scroll, final LinearLayout container, final int time){
        scroll.post(new Runnable() {
            @Override
            public void run() {
                switch (time){
                    case (24):
                        scroll.setScrollY((container.getMeasuredHeight()/(time+2))*3+((container.getMeasuredHeight()/(time+2)*hour-scroll.getMeasuredHeight())));
                        break;
                    case (60):
                        scroll.setScrollY((container.getMeasuredHeight()/(time+2))*3+((container.getMeasuredHeight()/(time+2)*minute-scroll.getMeasuredHeight())));
                        break;
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTimeChangeListener(final ScrollView scroll, final LinearLayout container, final int time){
        scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int newPart = Math.round(scrollY+v.getMeasuredHeight()/2)/(container.getMeasuredHeight()/(time+2));
                int oldPart = Math.round(oldScrollY+v.getMeasuredHeight()/2)/(container.getMeasuredHeight()/(time+2));
                ((TextView)container.findViewWithTag(""+oldPart)).setTextColor(textColorSub2);
                ((TextView)container.findViewWithTag(""+newPart)).setTextColor(textColor2);
                if (time == 24){ hour = newPart - 1; }
                if (time == 60){ minute = newPart - 1;}
            }
        });
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    int newPart = Math.round(v.getScrollY()+v.getMeasuredHeight()/2)/(container.getMeasuredHeight()/(time+2));
                    ValueAnimator scrollAnim = ValueAnimator.ofInt(v.getScrollY(), container.getMeasuredHeight()/(time+2)*(newPart-1));
                    scrollAnim.setDuration(200);
                    scrollAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            v.scrollTo(0, (Integer) animation.getAnimatedValue());
                        }
                    });
                    scrollAnim.start();
                    return true;
                }else{ return false; }
            }
        });
    }

    public void makeDateButton(final View root){
        Button dateBtn = root.findViewById(R.id.add_task_dialog_date);
        if (repeat){
            dateBtn.setText(getContext().getString(R.string.repeat)+":\n");
            dateBtn.setTextSize(16);
            int daysCount = 0;
            for (int i = 0; i < 7; i++){
                ArrayMap day = (ArrayMap) weekDaysStates.get(i);
                Boolean state = (boolean)day.get("state");
                if (state){
                    dateBtn.setText(dateBtn.getText()+weekDays[i]+"  ");
                    daysCount += 1;
                }
            }
            if (daysCount == 7){ dateBtn.setText(getContext().getString(R.string.repeat)+":\n"+getContext().getString(R.string.everyday)); }
        }else{
            String sDay = ""+day;
            String sMonth = ""+month;
            if (sDay.length() == 1){ sDay = "0"+sDay;}
            if (sMonth.length() == 1){ sMonth = "0"+sMonth;}
            dateBtn.setText(""+sDay+"."+sMonth+"."+year);
        }
        root.post(new Runnable() {
            @Override
            public void run() {
               root.findViewWithTag("calendarOpen1").setVisibility(View.GONE);
            }
        });
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarOpen == false){
                    for (int i = 0; i < 3; i++){
                        root.findViewWithTag("calendarClose"+i).setVisibility(View.GONE);
                    }
                    for (int i = 0; i < 6; i++){
                        root.findViewWithTag("calendarOpen"+i).setVisibility(View.VISIBLE);
                    }
                    root.findViewById(R.id.add_task_dialog_delete).setVisibility(View.GONE);
                    calendarOpen = true;
                }
            }
        });
    }

    public void makeCalendarOkButton(final View root){
        Button btn = root.findViewById(R.id.add_task_dialog_date_ok);
        final DBManager db = new DBManager(getContext());
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (calendarOpen == true){
                    for (int i = 0; i < 3; i++){
                        root.findViewWithTag("calendarClose"+i).setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < 6; i++){
                        root.findViewWithTag("calendarOpen"+i).setVisibility(View.GONE);
                    }
                    if (repeat){
                        root.findViewById(R.id.task_type_button_panel).setVisibility(View.GONE);
                        deadline = false;
                        ((Button)root.findViewById(R.id.add_task_dialog_type_in)).setTextColor(textColor2);
                        ((Button)root.findViewById(R.id.add_task_dialog_type_to)).setTextColor(textColorSub2);
                    }
                    if (db.CheckTask(elementId)){ root.findViewById(R.id.add_task_dialog_delete).setVisibility(View.VISIBLE);}
                    calendarOpen = false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makeWeekDaysButtons(final View v){

        for (int i = 1; i < 8; i++){
            Button dayBtn = v.findViewWithTag("day"+i);
            final int finalI = i;
            dayBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View btn) {
                    repeat = true;
                    if ((Boolean) (((ArrayMap)weekDaysStates.get(finalI -1)).get("state"))){
                        ((Button)btn).setTextColor(textColorSub2);
                        ((ArrayMap)weekDaysStates.get(finalI -1)).put("state", false);
                        updateDateInfo(v);
                    }else{
                        ((Button)btn).setTextColor(textColor2);
                        ((ArrayMap)weekDaysStates.get(finalI -1)).put("state", true);
                        updateDateInfo(v);
                    }
                }
            });
            if ((Boolean) ((ArrayMap)weekDaysStates.get(i-1)).get("state")){ dayBtn.setTextColor(textColor2);}
        }
    }

    public void makeCalendar(final View v){
        CalendarView calendar = v.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int cYear, int cMonth, int cDay) {
                year = cYear;
                month = cMonth;
                day = cDay;
                repeat = false;
                updateDateInfo(v);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateDateInfo(View v){
        Button dateBtn = v.findViewById(R.id.add_task_dialog_date);
        dateBtn.setText("");
        if (repeat){
            dateBtn.setTextSize(16);
            dateBtn.setText(getContext().getString(R.string.repeat)+":\n");
            ArrayList currentDays = new ArrayList();
            for (int i = 0; i < 7; i++){
                if ((Boolean) ((ArrayMap)weekDaysStates.get(i)).get("state")){
                    currentDays.add(((ArrayMap)weekDaysStates.get(i)).get("name"));
                }
            }
            if(currentDays.size() == 0){
                repeat = false;
                updateDateInfo(v);
            }
            if(currentDays.size() == 7){dateBtn.setText(dateBtn.getText()+getContext().getString(R.string.everyday));}
            else{
                for (int i = 0; i < currentDays.size(); i++){
                    dateBtn.setText(dateBtn.getText()+(String)currentDays.get(i)+"  ");
                }
            }
            deadline = false;
        }else{
            dateBtn.setTextSize(22);
            String sDay = ""+day;
            String sMonth = ""+month;
            if (sDay.length() == 1){ sDay = "0"+sDay;}
            if (sMonth.length() == 1){ sMonth = "0"+sMonth;}
            dateBtn.setText(""+sDay+"."+sMonth+"."+year);
            for (int i = 1; i < 8; i++){
                ((Button)v.findViewWithTag("day"+i)).setTextColor(textColorSub2);
                ((ArrayMap)weekDaysStates.get(i-1)).put("state", false);
            }

        }
    }

    public void makeCancelButton(View v){
        Button cancelBtn = v.findViewById(R.id.add_task_dialog_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void makeOkButton(View v){
        Button btn = v.findViewById(R.id.add_task_dialog_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onClick(View btn) {
                DBManager db = new DBManager(getContext());
                int type = 0;
                String date = "";
                String time = ""+hour+"/"+minute;
                if (deadline) {type = 1;}
                if (repeat) {
                    type = 2;
                    for (int i = 0; i < weekDaysStates.size(); i ++){
                        if ((boolean)((ArrayMap)weekDaysStates.get(i)).get("state")){
                            date += i;
                        }
                    }
                }else{
                    date = day+"/"+month+"/"+year;
                }
                if (db.CheckTask(elementId)){
                    db.UpdateTask(elementId, date, time, type);
                }else{
                    db.PutTask(elementId, date, time, type);
                }
                ((TextView)parrentView.findViewById(R.id.node_open_btn_task_txt)).setText(getContext().getString(R.string.edit_task));
                MainActivity.notificationAlarm.restart();
                dismiss();
            }
        });
    }

    public void makeDeleteButton(View v){
        final DBManager db = new DBManager(getContext());
        Button btn = v.findViewById(R.id.add_task_dialog_delete);
        if (db.CheckTask(elementId)){
            btn.setVisibility(View.VISIBLE);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onClick(View btn) {
                db.DeleteTask(elementId);
                ((TextView)parrentView.findViewById(R.id.node_open_btn_task_txt)).setText(getContext().getString(R.string.add_task));
                dismiss();
            }
        });
    }

    public void getData(View v){
        DBManager db = new DBManager(getContext());

        for (int i = 0; i < 7; i++){
            ArrayMap weekDayMap = new ArrayMap();
            weekDayMap.put("name", weekDays[i]);
            weekDayMap.put("state", false);
            weekDaysStates.add(weekDayMap);
        }

        if (db.CheckTask(elementId)){
            ArrayMap task = db.GetTask(elementId);
            String[] time = (String[])((String) task.get("time")).split("/");
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);

            switch ((int)task.get("type")){
                case (0):
                    deadline = false;
                    repeat = false;
                    break;
                case (1):
                    deadline = true;
                    repeat = false;
                    break;
                case (2):
                    deadline = false;
                    repeat = true;
                    break;
            }
            if (repeat){
                for (int i = 0; i < task.get("date").toString().length(); i++){
                    String day = ""+task.get("date").toString().charAt(i);
                    int dayNum = Integer.parseInt(day);
                    ((ArrayMap)weekDaysStates.get(dayNum)).put("state", true);
                }
            }else{
                day = Integer.parseInt((task.get("date").toString().split("/"))[0]);
                month = Integer.parseInt((task.get("date").toString().split("/"))[1]);
                year = Integer.parseInt((task.get("date").toString().split("/"))[2]);
            }
        }else{
            Calendar calendar = new GregorianCalendar();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            hour = Integer.parseInt((calendar.getTime().toString().split(" ")[3]).toString().split(":")[0]);
            minute = Integer.parseInt((calendar.getTime().toString().split(" ")[3]).toString().split(":")[1]);
        }
    }
}
