package com.larje.taskmanager.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.OptionsActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.task.content.DayContent;
import com.larje.taskmanager.task.content.DeadlineContent;
import com.larje.taskmanager.task.content.KategoryContent;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class TaskScreen {

    private Context context;
    private int filterOption;
    private int scroll;

    public TaskScreen(Context context, int filterOption, int scroll){
        this.context = context;
        this.filterOption = filterOption;
        this.scroll = scroll;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ScrollView getTaskScreen(){
        ScrollView scroll = new ScrollView(context);
        scroll.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        scroll.requestLayout();
//        scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                PlaceholderFragment.taskScrollY = scrollY;
//            }
//        });

        scroll.addView(makeTaskScreenRoot());

        return scroll;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private LinearLayout makeTaskScreenRoot(){
        LinearLayout taskScreenRoot = new LinearLayout(context);
        taskScreenRoot.setOrientation(LinearLayout.VERTICAL);
        taskScreenRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        taskScreenRoot.addView(makeOptionsPanel());
        taskScreenRoot.addView(makeContent());

        return taskScreenRoot;
    }

    private LinearLayout makeOptionsPanel(){
        View panelRoot = MainActivity.inflater.inflate(R.layout.task_options_panel, null, false);
        LinearLayout panel = panelRoot.findViewById(R.id.task_options_panel);
        LinearLayout.LayoutParams panelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        panelParams.setMargins(20, 40, 20, 20);
        panel.setLayoutParams(panelParams);
        makeOptionsSpinner(panel);
        makeOptionsButton(panel);
        return panel;
    }

    private void makeOptionsSpinner(LinearLayout panel){
        ArrayList<String> options = new ArrayList<String>();
        options.add(context.getString(R.string.filter_option_1));
        options.add(context.getString(R.string.filter_option_2));
        options.add(context.getString(R.string.filter_option_3));

        Spinner spinner = panel.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_row, R.id.spinner_row_text, options);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (filterOption != position){
                    PlaceholderFragment.taskFilterOption = position;
                    MainActivity.view.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private LinearLayout makeContent(){
        LinearLayout contentRoot = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentRoot.setLayoutParams(params);
        switch (filterOption){
            case 0:
                DayContent dayContent = new DayContent(context);
                contentRoot = dayContent.getContent();
                break;
            case 1:
                KategoryContent kategoryContent = new KategoryContent(context);
                contentRoot = kategoryContent.getContent();
                break;
            case 2:
                DeadlineContent deadlineContent = new DeadlineContent(context);
                contentRoot = deadlineContent.getContent();
                break;
        }
        return contentRoot;
    }

    private void makeOptionsButton(LinearLayout panel){
        ImageView btn = panel.findViewById(R.id.options_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(MainActivity.mainToOptionsIntent);
            }
        });
    }
}
