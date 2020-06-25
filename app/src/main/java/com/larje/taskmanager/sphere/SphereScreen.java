package com.larje.taskmanager.sphere;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Vibrator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBCreator;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

import java.util.ArrayList;

import static android.service.autofill.Validators.and;
import static android.service.autofill.Validators.or;


public class SphereScreen {

    Context context;
    int openedSphereId;
    int closedSphereId;
    Boolean anim;
    FragmentManager fm;
    ValueAnimator sphere_anim;
    ValueAnimator sphere_anim_close;
    ValueAnimator kats_anim;
    ValueAnimator kats_anim_close;

    int primary;
    int primaryDark;
    int backgroundColor;

    public SphereScreen(Context context, int openedSphereId, int closedSphereId, FragmentManager fm, boolean anim) {
        this.context =  context;
        this.openedSphereId = openedSphereId;
        this.closedSphereId = closedSphereId;
        this.fm = fm;
        this.anim = anim;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ScrollView Create(){

        switch (MainActivity.theme){
            case 0:
                primary = context.getColor(R.color.primary);
                primaryDark = context.getColor(R.color.primaryDark2);
                backgroundColor = context.getColor(R.color.background);
                break;
            case 1:
                primary = context.getColor(R.color.primaryD);
                primaryDark = context.getColor(R.color.primaryDarkD);
                backgroundColor = context.getColor(R.color.backgroundD);
                break;
        }
        DBManager db = new DBManager(context);

        final ScrollView scroll = new ScrollView(context);
        ScrollView.LayoutParams scroll_params = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scroll.setLayoutParams(scroll_params);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(20,20,20,20);
        root.setLayoutParams(params);
        root.setBackgroundColor(backgroundColor);



        ArrayList data = db.GetChildren(-1);
        if (data.size() == 0){
            makeNoDataFiller(root, scroll);
        }
        for (int i = 0; i < data.size(); i++){
            final ArrayMap element = (ArrayMap) data.get(i);
            String[] clr = new String[3];
            if (element.get("color").toString() != "none") {
                clr = element.get("color").toString().split(":");
            }
            else {
                clr[0] = "255";
                clr[1] = "255";
                clr[2] = "255";
            }
            final LinearLayout sphere = new LinearLayout(context);
            sphere.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams sphere_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            sphere_params.setMargins(0,20,0,20);
            sphere.setLayoutParams(sphere_params);
            sphere.setTag(element.get("id"));
            sphere.setBackground(context.getDrawable(R.drawable.rounded_sphere_root));
            sphere.setElevation(5);


            class Topline extends View {
                String[] clr;
                public Topline(Context context, String[] clr) {
                    super(context);
                    this.clr = clr;
                }
                protected void onDraw(Canvas canvas){
                    Paint paint = new Paint();
                    paint.setColor(Color.rgb(Integer.parseInt(clr[0]), Integer.parseInt(clr[1]), Integer.parseInt(clr[2])));
                    RectF rect = new RectF();
                    rect.set(0,0,canvas.getWidth(),canvas.getHeight());
                    canvas.drawRoundRect(rect, 16, 16, paint);
                    paint.setColor(Color.rgb(Integer.parseInt(clr[0]), Integer.parseInt(clr[1]), Integer.parseInt(clr[2])));
                    rect.set(0,this.getHeight()/2,canvas.getWidth(),canvas.getHeight());
                    canvas.drawRect(rect, paint);
                }
            }
            Topline topLine = new Topline(context, clr);
            topLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));

            class Bottomline extends View {
                String[] clr;
                public Bottomline(Context context, String[] clr) {
                    super(context);
                    this.clr = clr;
                }
                protected void onDraw(Canvas canvas){
                    Paint paint = new Paint();
                    paint.setColor(primaryDark);
                    RectF rect = new RectF();
                    rect.set(0,0,canvas.getWidth(),canvas.getHeight());
                    canvas.drawRoundRect(rect, 16, 16, paint);
                    paint.setColor(primaryDark);
                    rect.set(0,0,canvas.getWidth(),canvas.getHeight()/2);
                    canvas.drawRect(rect, paint);
                }
            }
            Bottomline bottomLine = new Bottomline(context, clr);
            bottomLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));

            LinearLayout centerLine = new LinearLayout(context);
            centerLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            centerLine.setPadding(0,20,0,20);
            centerLine.setTag("centerline");

//            TypedValue centerLineBackground = new TypedValue();
//            context.getTheme().resolveAttribute(R.attr.tab1, centerLineBackground, true);
//            centerLine.setBackgroundColor(centerLineBackground.data);
            centerLine.setBackgroundColor(primary);

            final TextView txt = new TextView(context);
            txt.setText(element.get("name").toString().toUpperCase());
            txt.setTextColor(context.getColor(R.color.darkThemeLevel1text));
            txt.setTextSize(20);
            txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            LinearLayout.LayoutParams txt_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txt_params.weight = 1;
            txt.setLayoutParams(txt_params);
            txt.setTypeface(MainActivity.face);

            if (closedSphereId == (int)sphere.getTag()) {
                txt.setPadding(80, 0, 0, 0);
                ValueAnimator sphere_anim_close = ValueAnimator.ofInt(80, 0);
                if (anim) {sphere_anim_close.setDuration(250);}
                else {sphere_anim_close.setDuration(0);}
                sphere_anim_close.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        txt.setPadding((int) animation.getAnimatedValue(), 0, 0, 0);
                        PlaceholderFragment.closed_sphereid = -1;
                    }
                });
                this.sphere_anim_close = sphere_anim_close;
            }

            if ((int) sphere.getTag() == openedSphereId) {
                ValueAnimator sphere_anim = ValueAnimator.ofInt(0, 80);
                if (anim) {sphere_anim.setDuration(250);}
                else {sphere_anim.setDuration(0);}
                sphere_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        txt.setPadding((int) animation.getAnimatedValue(), 0, 0, 0);
                    }
                });
                this.sphere_anim = sphere_anim;
            }

            TextView add_btn = new TextView(context);
            add_btn.setText("+");
            add_btn.setTypeface(MainActivity.face);
            add_btn.setTextColor(Color.rgb(Integer.parseInt(clr[0]), Integer.parseInt(clr[1]), Integer.parseInt(clr[2])));
            add_btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            add_btn.setTextSize(20);
            LinearLayout.LayoutParams add_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            add_btn.setPadding(30,0,30,0);
            add_btn.setLayoutParams(add_params);
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment addElementDialog = new AddElementDialog((int) element.get("id"), 1);
                    PlaceholderFragment.opened_sphereid = (int) element.get("id");
                    addElementDialog.show(fm, "dlg");
                }
            });

            centerLine.addView(txt);
            centerLine.addView(add_btn);
            sphere.addView(topLine);
            sphere.addView(centerLine);

            if ((int) sphere.getTag() == openedSphereId){
                KategoryScreen kategoryScreen = new KategoryScreen(context, (int) sphere.getTag(), (String[]) clr, fm);
                final View katsOpened = kategoryScreen.Create();
                ValueAnimator kats_anim = ValueAnimator.ofInt(0, kategoryScreen.maxHeight);
                if (anim) {kats_anim.setDuration(250);}
                else {kats_anim.setDuration(0);}
                kats_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        katsOpened.getLayoutParams().height = (int) animation.getAnimatedValue();
                    }
                });
                this.kats_anim = kats_anim;
                sphere.addView(katsOpened);

            }

            if ((int) sphere.getTag() == closedSphereId){
                KategoryScreen kategoryScreen = new KategoryScreen(context, (int) sphere.getTag(), (String[]) clr, fm);
                final View katsClosed = kategoryScreen.Create();
                ValueAnimator kats_anim_close = ValueAnimator.ofInt(kategoryScreen.maxHeight, 0);
                if (anim) {kats_anim_close.setDuration(250);}
                else {kats_anim_close.setDuration(0);}
                kats_anim_close.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        katsClosed.getLayoutParams().height = (int) animation.getAnimatedValue();
                        PlaceholderFragment.closed_sphereid = -1;
                    }
                });
                this.kats_anim_close = kats_anim_close;
                sphere.addView(katsClosed);
            }

            sphere.addView(bottomLine);

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (openedSphereId == (int) v.getTag()){
                        PlaceholderFragment.opened_sphereid = -1;
                        PlaceholderFragment.closed_sphereid = (int) v.getTag();
                    }
                    else {
                        PlaceholderFragment.closed_sphereid = openedSphereId;
                        PlaceholderFragment.opened_sphereid = (int) v.getTag();}
                    MainActivity.view.getAdapter().notifyDataSetChanged();
                }
            };
            sphere.setOnClickListener(ocl);
            final DialogFragment[] contextDialog = new DialogFragment[1];
            sphere.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    contextDialog[0] = new ContextElementDialog((int) v.getTag(), 0);
                    contextDialog[0].show(fm, "editDialog");
                    ValueAnimator tch_up = ValueAnimator.ofInt(Color.red(primary)-14, Color.red(primary));
                    tch_up.setDuration(200);
                    tch_up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            v.findViewWithTag("centerline").setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
                        }
                    });
                    tch_up.start();
                    Vibrator vibro = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                    if(vibro.hasVibrator()){vibro.vibrate(50);}
                    return false;
                }
            });
            sphere.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    @SuppressLint("ClickableViewAccessibility") ValueAnimator tch_down = ValueAnimator.ofInt(Color.red(primary), Color.red(primary)-14);
                    tch_down.setDuration(500);
                    tch_down.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            v.findViewWithTag("centerline").setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
                            if ((int) animation.getAnimatedValue() == Color.red(primary)-14){
                                ValueAnimator anim_back = ValueAnimator.ofInt(Color.red(primary)-14, Color.red(primary));
                                anim_back.setDuration(100);
                                anim_back.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        v.findViewWithTag("centerline").setBackgroundColor(Color.rgb((int)animation.getAnimatedValue(),(int) animation.getAnimatedValue(),(int)animation.getAnimatedValue()));
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

            root.addView(sphere);
       }
        if (data.size() > 0) {
            TextView bottom_space = new TextView(context);
            bottom_space.setText(" ");
            bottom_space.setPadding(0, 70, 0, 70);
            root.addView(bottom_space);
        }

        scroll.addView(root);
        scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    PlaceholderFragment.scrolly = scrollY;
                }
            });

        return scroll;
    }


    private void makeNoDataFiller(final LinearLayout root, final ScrollView scroll){
        scroll.post(new Runnable() {
            @Override
            public void run() {
                TextView filler = new TextView(context);
                filler.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scroll.getMeasuredHeight()));
                filler.setText(context.getString(R.string.sphere_filler));
                filler.setTextSize(30);
                filler.setGravity(Gravity.CENTER);
                filler.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                filler.setTypeface(MainActivity.face);
                switch (MainActivity.theme){
                    case 0:
                        filler.setTextColor(Color.parseColor("#7B1FA2"));
                        break;
                    case 1:
                        filler.setTextColor(Color.parseColor("#CE93D8"));
                        break;
                }
                root.setGravity(Gravity.CENTER);
                root.addView(filler);
            }
        });
    }

    public void StartAnim(){
        //ValueAnimator sphere_anim = this.sphere_anim;
        //Log.d("anim", (String) sphere_anim.toString());
        try{this.sphere_anim.start();}
        catch (java.lang.NullPointerException e){}
        try{this.sphere_anim_close.start();}
        catch (java.lang.NullPointerException e){}
        try{this.kats_anim.start();}
        catch (java.lang.NullPointerException e){}
        try{this.kats_anim_close.start();}
        catch (java.lang.NullPointerException e){}
    }
}


