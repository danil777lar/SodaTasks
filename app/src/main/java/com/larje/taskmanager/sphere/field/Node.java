package com.larje.taskmanager.sphere.field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.zip.Inflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static java.security.AccessController.getContext;

public class Node extends View{

    Context context;
    int elementId;
    private FragmentManager fm;

    public Node(Context context, int elementId, FragmentManager fm){
        super(context);
        this.context = context;
        this.elementId = elementId;
        this.fm = fm;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    public View getNode(){
        DBManager db = new DBManager(context);

        LinearLayout root = new LinearLayout(context);
        //root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        root.setBackgroundColor(Color.rgb(255, 255, 255));
        root.setOrientation(LinearLayout.VERTICAL);

        TextView nodeName = new TextView(context);
        LinearLayout.LayoutParams name_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        name_params.weight = 1;
        name_params.setMargins(0,0,0,0);
        nodeName.setPadding(0,0,0,0);
        nodeName.setGravity(Gravity.CENTER);
        nodeName.setLayoutParams(name_params);
        nodeName.setText((String) db.GetElement(elementId).get("name").toString().toUpperCase());

        int color = 0;
        int colorDone = 0;
        switch (MainActivity.theme){
            case 0:
                colorDone = context.getColor(R.color.darkThemeLevel1text);
                color = context.getColor(R.color.darkThemeLevel2subtext);
                break;
            case 1:
                color = context.getColor(R.color.darkThemeLevel1text);
                colorDone = context.getColor(R.color.darkThemeLevel2subtext);
                break;
        }
        if ((int)db.GetElement(elementId).get("status") == 0){
            nodeName.setTextColor(color);
            root.setBackgroundResource(R.drawable.rounded_layout);
        } else {
            nodeName.setTextColor(colorDone);
            root.setBackgroundResource(R.drawable.rounded_level2);
        }
        nodeName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        nodeName.setTag("nodeName");
//        nodeName.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        nodeName.setTypeface(MainActivity.face);
        root.addView(nodeName);

//        View line = new View(context);
//        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
//        line.setBackgroundColor(Color.parseColor("#000000"));
//        root.addView(line);

        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if((event.getPointerCount() == 1)){
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        NodeOpen dlg = new NodeOpen(elementId);
                        dlg.show(fm, null);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

//        LinearLayout btnPnl = new LinearLayout(context);
//        btnPnl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        btnPnl.setOrientation(LinearLayout.HORIZONTAL);
//        root.addView(btnPnl);
//
//        Button add_btn = new Button(context);
//        //add_btn.setImageResource(R.drawable.ic_launcher_foreground);
//        add_btn.setBackgroundColor(Color.parseColor("#00000000"));
//        add_btn.setText("+");
//        add_btn.setTextColor(Color.parseColor("#000000"));
//        btnPnl.addView(add_btn);
//
//        Button delete_btn = new Button(context);
//        //delete_btn.setImageResource(R.drawable.ic_launcher_foreground);
//        delete_btn.setBackgroundColor(Color.parseColor("#00000000"));
//        delete_btn.setText("-");
//        delete_btn.setTextColor(Color.parseColor("#000000"));
//        btnPnl.addView(delete_btn);

        return root;
    }

}
