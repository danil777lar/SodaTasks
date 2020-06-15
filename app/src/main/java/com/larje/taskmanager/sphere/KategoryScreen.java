package com.larje.taskmanager.sphere;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Vibrator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

import java.util.ArrayList;

public class KategoryScreen extends View{

    Context context;
    int parent;
    String[] clr = new String[3];
    FragmentManager fm;
    int maxHeight = 0;
    int backgroundColor;
    int textColor;
    int subTextColor;

    public KategoryScreen(Context context, int parent, String[] clr, FragmentManager fm) {
        super(context);
        this.context =  context;
        this.parent = parent;
        this.clr = clr;
        this.fm = fm;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
    public View Create(){
        switch (MainActivity.theme){
            case 0:
                backgroundColor = context.getColor(R.color.primaryDark2);
                textColor = context.getColor(R.color.darkThemeLevel2text);
                subTextColor = context.getColor(R.color.darkThemeLevel2text);
                break;
            case 1:
                backgroundColor = context.getColor(R.color.primaryDarkD);
                textColor = context.getColor(R.color.darkThemeLevel2text);
                subTextColor = context.getColor(R.color.darkThemeLevel2subtext);
                break;
        }

        DBManager db = new DBManager(context);

        //Основной слой
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(params);
        root.setBackgroundColor(backgroundColor);
        ///////////////


        ArrayList data = db.GetChildren(this.parent);
        for (int i = 0; i < data.size(); i++){
            ArrayMap element = (ArrayMap) data.get(i);

            View line;
            if (i == data.size()-1){
                line = makeDivider(0);
            } else {
                line = makeDivider(10);
            }

            Button btn = new Button(context);
            btn.setText((String) element.get("name"));
            btn. setTextSize(15);
//            btn.setBackgroundColor(context.getColor(R.color.darkThemeLevel2));
            btn.setBackgroundColor(backgroundColor);

            if ((int)element.get("status") == 0) {
                btn.setTextColor(textColor);
            }else if((int) element.get("status") == 1){
                btn.setTextColor(subTextColor);
                btn.setPaintFlags(btn.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            btn.setTag(element.get("id"));
            btn.setWidth(root.getWidth());
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaceholderFragment.spherelvl = 1;
                    PlaceholderFragment.kategoryId = (int)v.getTag();
                    MainActivity.view.getAdapter().notifyDataSetChanged();
                }
            };
            btn.setOnClickListener(ocl);
            final DialogFragment[] contextDialog = new DialogFragment[1];
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    contextDialog[0] = new ContextElementDialog((int) v.getTag(), 1);
                    contextDialog[0].show(fm, "editDialog");
                    ValueAnimator tch_up = ValueAnimator.ofInt(Color.red(backgroundColor)-13, Color.red(backgroundColor));
                    tch_up.setDuration(200);
                    tch_up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            v.setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
                        }
                    });
                    tch_up.start();
                    Vibrator vibro = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                    if(vibro.hasVibrator()){vibro.vibrate(50);}
                    return false;
                }
            });
            btn.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    ValueAnimator tch_down = ValueAnimator.ofInt(Color.red(backgroundColor), Color.red(backgroundColor)-13);
                    tch_down.setDuration(500);
                    tch_down.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            v.setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
                            if ((int) animation.getAnimatedValue() == Color.red(backgroundColor)-13){
                                ValueAnimator anim_back = ValueAnimator.ofInt(Color.red(backgroundColor)-13, Color.red(backgroundColor));
                                anim_back.setDuration(100);
                                anim_back.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        v.setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
                                    }
                                });
                                anim_back.start();
                            }
                        }
                    });
                    if (contextDialog[0] == null){tch_down.start();}
                    else if(!contextDialog[0].isVisible()){tch_down.start();}
                    return false;
                }
            });

            root.addView(btn);
            root.addView(line);
            maxHeight += btn.getLayoutParams().height;
            maxHeight += line.getLayoutParams().height;
        }
        //scroll.addView(root);
        //bottom.addView(root);
        if (data.size() == 0)
        {
            Button empty = new Button(context);
            empty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
            empty.setText(context.getString(R.string.no_items));
            empty.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            empty.setTextColor(subTextColor);
            empty.setBackgroundColor(backgroundColor);
            empty.setTextSize(15);
            root.addView(empty);
            root.addView(makeDivider(0));

            maxHeight += empty.getLayoutParams().height;
            maxHeight += 2;
        }
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View makeDivider(int margin){
        View line = new View(context);
        LinearLayout.LayoutParams line_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        line_params.setMargins(margin, 0, margin, 0);
        line.setLayoutParams(line_params);
        switch (MainActivity.theme){
            case 0:
                line.setBackgroundColor(Color.argb(150, Integer.parseInt(clr[0]), Integer.parseInt(clr[1]), Integer.parseInt(clr[2])));
                break;
            case 1:
                line.setBackgroundColor(Color.rgb(Integer.parseInt(clr[0])/2, Integer.parseInt(clr[1])/2, Integer.parseInt(clr[2])/2));
                break;
        }
        return line;
    }
}
