package com.larje.taskmanager.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ColorPicker extends View{
    public int cx;


    public ColorPicker(Context context) {
        super(context);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
        init(context);
    }

    private void init(Context context){
    }


    @Override
    protected void onDraw(Canvas c){
        RectF rect = new RectF();
        rect.set(0,0, c.getWidth(), c.getHeight());

        int[] clrs = new int[]{Color.rgb(255, 145, 145),Color.rgb(255, 255, 145), Color.rgb(145, 255, 145),Color.rgb(145, 255, 255), Color.rgb(145, 145, 255),Color.rgb(255, 145, 255), Color.rgb(255, 145, 145)};
        Shader s = new LinearGradient(0, 0, c.getWidth(), c.getHeight(), clrs, null, Shader.TileMode.MIRROR);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(s);
        c.drawRect(rect, paint);
    }

    public static ArrayMap GetColor(int width, int x){
        int r = 145;
        int g = 145;
        int b = 145;
        ArrayMap point = new ArrayMap();
        point.put("R1", (width/6)*0);
        point.put("G",  (width/6)*1);
        point.put("B",  (width/6)*3);
        point.put("R2", (width/6)*5);
        point.put("R1l", (width/6)*1);
        point.put("Gl",  (width/6)*3);
        point.put("Bl",  (width/6)*5);
        point.put("R2l", (width/6)*6);
        int raznica = 0;
        if (x < width/2){
            if (x < (int)point.get("R1")){
                raznica = Math.max((int)point.get("R1"), (int)x) - Math.min((int)point.get("R1"), (int)x);
            }
            else if (x > (int)point.get("R1l")){
                raznica = Math.max((int)point.get("R1l"), (int)x) - Math.min((int)point.get("R1l"), (int)x);
            }else{raznica = 0;}

            r += 110 - (110.0/((float)width/6))*raznica;
        }
        else {
            if (x < (int)point.get("R2")){
                raznica = Math.max((int)point.get("R2"), (int)x) - Math.min((int)point.get("R2"), (int)x);
            }
            else if (x > (int)point.get("R2l")){
                raznica = Math.max((int)point.get("R2l"), (int)x) - Math.min((int)point.get("R2l"), (int)x);
            }else{raznica = 0;}
            r += 110 - (110.0/((float)width/6))*raznica;
        }

        if (x < (int)point.get("G")){
            raznica = Math.max((int)point.get("G"), (int)x) - Math.min((int)point.get("G"), (int)x);
        }
        else if (x > (int)point.get("Gl")){
            raznica = Math.max((int)point.get("Gl"), (int)x) - Math.min((int)point.get("Gl"), (int)x);
        }else{raznica = 0;}
        g += 110 - (110.0/((float)width/6))*raznica;

        if (x < (int)point.get("B")){
            raznica = Math.max((int)point.get("B"), (int)x) - Math.min((int)point.get("B"), (int)x);
        }
        else if (x > (int)point.get("Bl")){
            raznica = Math.max((int)point.get("Bl"), (int)x) - Math.min((int)point.get("Bl"), (int)x);
        }else{raznica = 0;}
        b += 110 - (110.0/((float)width/6))*raznica;
        if (r < 145) {r = 145;}
        if (g < 145) {g = 145;}
        if (b < 145) {b = 145;}

        ArrayMap colors = new ArrayMap();
        colors.put("R", r);
        colors.put("G", g);
        colors.put("B", b);
        return colors;
    }
}

























