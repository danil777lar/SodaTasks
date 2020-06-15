package com.larje.taskmanager.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;

public class LinedBackground extends Drawable {


    ArrayList childList;
    ConstraintLayout field;
    DBManager db;
    Context context;
    int nodeWidth;

    public  LinedBackground(ArrayList childList, ConstraintLayout field, Context context, int nodeWidth){
        this.childList = childList;
        this.field = field;
        this.db = new DBManager(context);
        this.nodeWidth = nodeWidth;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void draw(@NonNull Canvas canv) {

        int color = 0;
        int colorDone = 0;
        switch (MainActivity.theme){
            case 0:
                colorDone = context.getColor(R.color.darkThemeLevel1subtext);
                color = context.getColor(R.color.darkThemeLevel2);
                break;
            case 1:
                color = context.getColor(R.color.darkThemeLevel1subtext);
                colorDone = context.getColor(R.color.darkThemeLevel2);
                break;
        }
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(nodeWidth/50);

        Paint paintDone = new Paint();
        paintDone.setColor(colorDone);
        paintDone.setStrokeWidth(nodeWidth/50);

        for (int i = 0; i < childList.size(); i++){
            ArrayMap element = (ArrayMap) childList.get(i);
            View node = field.getViewById((int)element.get("id"));
            ArrayList nodeChildren = db.GetChildren((int)element.get("id"));

            for (int k = 0; k < nodeChildren.size(); k++){
                ArrayMap nodeChildMap = (ArrayMap) nodeChildren.get(k);
                View nodeChild =  field.getViewById((int)nodeChildMap.get("id"));

                canv.drawLine(node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2), node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), paintDone);
                canv.drawLine(node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), nodeChild.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), paintDone);
                canv.drawLine(nodeChild.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), nodeChild.getX()+(nodeWidth/2), nodeChild.getY(), paintDone);
            }
            for (int k = 0; k < nodeChildren.size(); k++){
                ArrayMap nodeChildMap = (ArrayMap) nodeChildren.get(k);
                View nodeChild =  field.getViewById((int)nodeChildMap.get("id"));

                if ((int)nodeChildMap.get("status") == 0){
                    canv.drawLine(node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2), node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), paint);
                    canv.drawLine(node.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), nodeChild.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), paint);
                    canv.drawLine(nodeChild.getX()+(nodeWidth/2), node.getY()+(nodeWidth/2)+(nodeWidth/4), nodeChild.getX()+(nodeWidth/2), nodeChild.getY(), paint);
                }
            }
        }
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
