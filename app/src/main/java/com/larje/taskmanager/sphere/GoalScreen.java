package com.larje.taskmanager.sphere;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;

public class GoalScreen {

    Context context;

    public GoalScreen(Context context){
        this.context = context;
    }

    public TextView getGoalScreen(){
        TextView filler = new TextView(context);
        filler.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        filler.setText(context.getString(R.string.goal_filler));
        filler.setGravity(Gravity.CENTER);
        filler.setTextSize(30);
        filler.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        filler.setTypeface(MainActivity.face);
        switch (MainActivity.theme){
            case 0:
                filler.setTextColor(Color.parseColor("#F57C00"));
                break;
            case 1:
                filler.setTextColor(Color.parseColor("#FFCC80"));
                break;
        }

        return filler;
    }
}
