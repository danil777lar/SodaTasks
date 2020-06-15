package com.larje.taskmanager.customview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.larje.taskmanager.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Zoom{

    Context context;
    float baseDist = 0;
    float dist = 0;
    float localScale = 0;
    public float scale = 1;

    ValueAnimator animHeight = ValueAnimator.ofInt(0,0);
    ValueAnimator animWidth = ValueAnimator.ofInt(0,0);

    public int startWidth = -1;
    public int startHeight = -1;

    float dx;
    float dy;

    public float perY;
    public float perX;
    public float centerY;
    public float centerX;
    public float oldScrollY = 0;
    public float oldScrollX = 0;

    public ScrollView scrollY;
    public HorizontalScrollView scrollX;
    public ConstraintLayout root;
    public RelativeLayout layout;
    TextView txt;

    public Zoom(Context context) {
        this.context = context;
        txt = new TextView(context);
    }

    @SuppressLint("ResourceType")
    public ConstraintLayout create(){
        root = new ConstraintLayout(context);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        layout = new RelativeLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setTag("layout");

        scrollY = new ScrollView(context);
        scrollX = new HorizontalScrollView(context);
        scrollY.setVerticalScrollBarEnabled(false);
        scrollX.setHorizontalScrollBarEnabled(false);
        scrollY.addView(scrollX);
        scrollX.addView(layout);

        root.addView(scrollY);

        setListeners();

        return root;
    }

    private float getDistance(MotionEvent event){
       float dx = event.getX(0) - event.getX(1);
       float dy = event.getY(0) - event.getY(1);
       return (float) Math.sqrt(dx*dx + dy*dy);
    }

    private void setScale(final float scale){
        layout.getLayoutParams().height = (int)(startHeight*scale);
        layout.getLayoutParams().width = (int)(startWidth*scale);
        Log.d("trbls", "h "+layout.getLayoutParams().height);
        Log.d("trbls", "w "+layout.getLayoutParams().width);

        layout.requestLayout();
    }

    private void setCenters(MotionEvent event){
        float x0 = event.getX(0);
        float x1 = event.getX(1);
        float y0 = event.getY(0);
        float y1 = event.getY(1);

        centerX = (Math.max(x0, x1) - (Math.max(x0, x1) - Math.min(x0, x1))/2)*scale;
        centerY = ((Math.max(y0, y1) - (Math.max(y0, y1) - Math.min(y0, y1))/2)*scale)-scrollY.getScrollY()*scale;
        perX = (centerX/(scrollX.getWidth()*scale));
        perY = (centerY/(scrollX.getHeight()));

        oldScrollY = scrollY.getScrollY();
        oldScrollX = scrollX.getScrollX();
//        Log.d("tch", "sxH = "+scrollX.getHeight());
//        Log.d("tch", "cч = "+centerX);
//        Log.d("tch", "cy = "+centerY);
//        Log.d("tch", "h = "+scrollX.getHeight());
//        Log.d("tch", "px = "+perX);
//        Log.d("tch", "py = "+perY);
    }

    private void saveScroll(){
    }

    private void setScroll(MotionEvent event, float localScale){
        int scrx = (int)(((startWidth*localScale)*perX) - (startWidth*perX));
        int scry = (int)(((startHeight*localScale)*perY) - (startHeight*perY));
//        int oldsx = (int)(oldScrollX - ((startWidth*localScale) - (startWidth+oldScrollX)));
//        int oldsy = (int)(oldScrollY - ((startHeight*localScale) - (startHeight+oldScrollY)));
        scrollX.setScrollX((int)((oldScrollX - ((startWidth*scale)*perX))+startWidth*perX) + scrx);
        scrollY.setScrollY((int)((oldScrollY - ((startHeight*scale)*perY))+startHeight*perY) + scry);
    }

    private void checkScale(){
        if (scale >= 1){}
        else{
            animHeight = ValueAnimator.ofInt(layout.getLayoutParams().height, startHeight);
            animWidth = ValueAnimator.ofInt(layout.getLayoutParams().width, startWidth);
            animHeight.setDuration(200);
            animWidth.setDuration(200);
            animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    layout.getLayoutParams().height = (int)a.getAnimatedValue();
                    layout.requestLayout();
                }
            });
            animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    layout.getLayoutParams().width = (int)a.getAnimatedValue();
                    layout.requestLayout();
                }
            });
            animHeight.start();
            animWidth.start();
            scale = 1;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2){
                    int mainaction = event.getAction()&MotionEvent.ACTION_MASK;
                    if (mainaction == MotionEvent.ACTION_POINTER_DOWN) {
                        baseDist = getDistance(event);
                        setCenters(event);
                        localScale = 1;
                    } else if (mainaction == MotionEvent.ACTION_POINTER_UP){
                        scale *= localScale;
                        checkScale();
                    } else {
                        if (!(animWidth.isRunning() | animWidth.isRunning())){
                            dist = getDistance(event);
                            localScale = dist/baseDist;
                            setScale(scale*localScale);
                            setScroll(event, scale*localScale);
                        }
                    }
                }
                return true;
            }
        });

        scrollX.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2){
                    int mainaction = event.getAction()&MotionEvent.ACTION_MASK;
                    if (mainaction == MotionEvent.ACTION_POINTER_DOWN) {
                        baseDist = getDistance(event);
                        setCenters(event);
                        localScale = 1;
                    } else if (mainaction == MotionEvent.ACTION_POINTER_UP){
                        scale *= localScale;
                        saveScroll();
                        checkScale();
                    } else {
                        if (!(animWidth.isRunning() | animWidth.isRunning())){
                            dist = getDistance(event);
                            localScale = dist/baseDist;
                            setScale(scale*localScale);
                            setScroll(event, scale*localScale);
                        }
                    }
                    return true;
                } else {return false;}
            }
        });

        scrollY.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2){
                    int mainaction = event.getAction()&MotionEvent.ACTION_MASK;
                    if (mainaction == MotionEvent.ACTION_POINTER_DOWN) {
                        baseDist = getDistance(event);
                        setCenters(event);
                        localScale = 1;
                    } else if (mainaction == MotionEvent.ACTION_POINTER_UP){
                        scale *= localScale;
                        saveScroll();
                        checkScale();
                    } else {
                        if (!(animWidth.isRunning() | animWidth.isRunning())){
                            dist = getDistance(event);
                            localScale = dist/baseDist;
                            setScale(scale*localScale);
                            setScroll(event, scale*localScale);
                        }
                    }
                    return true;
                } else {return false;}
            }
        });

        root.post(new Runnable() {
            @Override
            public void run() {
                layout.getLayoutParams().width = root.getMeasuredWidth();
                layout.requestLayout();
                startWidth = root.getMeasuredWidth();
            }
        });
    }
}

