package com.larje.taskmanager.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;

public class RoundedBackground extends Drawable {

    Context context;
    int color;
    boolean topLeft;
    boolean topRight;
    boolean bottomRight;
    boolean bottomLeft;

    public RoundedBackground(Context context, int color, boolean topLeft, boolean topRight, boolean bottomRight, boolean bottomLeft){
        this.context = context;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.color = color;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void draw(@NonNull Canvas canv) {
        Paint paint = new Paint();
        paint.setColor(color);


        RectF rect = new RectF();
        rect.set(0,0,canv.getWidth(),canv.getHeight()*2);
        canv.drawRoundRect(rect, 25, 25, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }
}
