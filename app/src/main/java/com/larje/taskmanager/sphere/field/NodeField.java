package com.larje.taskmanager.sphere.field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.larje.taskmanager.customview.LinedBackground;
import com.larje.taskmanager.customview.Zoom;
import com.larje.taskmanager.data.DBManager;

import java.util.ArrayList;

public class NodeField {

    private Context context;
    private int elementId;
    DBManager db;
    private ArrayList childList = new ArrayList(); // Список всех элементов
    private ArrayList elementsOfLevels = new ArrayList(); // Список уровней и элементов на них
    private ArrayMap widthOfElements = new ArrayMap(); // Словарь элементов и их ширины [id:width]
    private FragmentManager fm;

    boolean bigger;
    int maxCount; // максимальное количество элементов на уровне
    int lvlCount; // количество уроыней
    RelativeLayout layout;
    Zoom zoom;
    ConstraintLayout field;
    ConstraintSet set;


    public NodeField(Context context, int elementId, FragmentManager fm){
        this.context = context;
        this.elementId = elementId;
        this.db = new DBManager(context);
        this.fm = fm;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View getNodeField(){
        zoom = new Zoom(context);
        ConstraintLayout root = zoom.create();

        layout = root.findViewWithTag("layout");
        layout.addView(getTree(elementId));
        setSizeChangeListener();

        return root;
    }

    public boolean hasChildren(){
        return (db.GetChildren(elementId).size() > 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ConstraintLayout getTree(int id){
        field = new ConstraintLayout(context);
        set = new ConstraintSet();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        field.setLayoutParams(params);
        childList = db.GetAllChildrenList(id);
        for(int i = 0; i < childList.size(); i++){
            ArrayMap element = (ArrayMap) childList.get(i);
            View node = getNode((int)element.get("id"));
            node.setId((int)element.get("id"));
            field.addView(node);
        }
        maxCount();
        setConstraints();

        return field;
    }

    private void maxCount(){
        int max = 0;
        int lvlNum = 0;
        ArrayMap lvls = new ArrayMap();
        ArrayList lvl = new ArrayList();
        lvl.add(db.GetElement(elementId).get("id"));
        lvls.put(lvlNum, lvl);
        elementsOfLevels.add(lvl);

        boolean w = true;
        while (w){
            w = false;
            lvlNum++;
            lvl = new ArrayList();
            ArrayList lastLvl = (ArrayList) lvls.get(lvlNum-1);

            for (int i = 0; i < lastLvl.size(); i++){
                int elementId = (int)lastLvl.get(i);
                ArrayList elementChildren = db.GetChildren(elementId);
                for (int k = 0; k < elementChildren.size(); k++){
                    ArrayMap elementChild = (ArrayMap) elementChildren.get(k);
                    lvl.add(elementChild.get("id"));
                    w = true;
                }
            }
            lvls.put(lvlNum, lvl);
            elementsOfLevels.add(lvl);
        }

        for (int i = 0; i < lvls.size(); i++){
            lvl = (ArrayList) lvls.get(i);
            if (lvl.size() > max){
                max = lvl.size();
            }
        }
        maxCount = max;
        lvlCount = lvls.size()-1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setConstraints(){
        set.clone(field);
        int nodeWidth = zoom.layout.getLayoutParams().width/(maxCount*2);
        ArrayMap element;


        //Размер текста узла
        for (int i = 0; i < childList.size(); i++){
            element = (ArrayMap) childList.get(i);
            set.constrainWidth((int)element.get("id"), nodeWidth);
            set.constrainHeight((int)element.get("id"), nodeWidth/2);
            TextView title = field.findViewById((int)element.get("id")).findViewWithTag("nodeName");
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50/(float)maxCount);
            title.setScaleX(((float)zoom.layout.getLayoutParams().width/zoom.startWidth));
            title.setScaleY(((float)zoom.layout.getLayoutParams().width/zoom.startWidth));
            title.requestLayout();
        }

        // Позиция первого элемента
        element = (ArrayMap) childList.get(0);
        set.connect((int)element.get("id"), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (layout.getLayoutParams().width/2)-nodeWidth/2);
        set.connect((int)element.get("id"), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (layout.getLayoutParams().width/2)-nodeWidth/2);

        // Позиционирование дерева посередине
        if (layout.getLayoutParams().height == -2){
            if (zoom.root.getMeasuredHeight() < (nodeWidth/2)*((lvlCount*2)-1)){
                zoom.startHeight = (nodeWidth/2)*(lvlCount+1)*2;
                field.setPadding(0,nodeWidth/2,0,nodeWidth/2);
                bigger = true;
            } else {
                zoom.startHeight = zoom.root.getMeasuredHeight();
                field.setPadding(0,(zoom.root.getMeasuredHeight()-(nodeWidth/2)*((lvlCount*2)-1))/2,0,(zoom.root.getMeasuredHeight()-(nodeWidth/2)*((lvlCount*2)-1))/2);

                bigger = false;
            }
        }else{
                if (bigger){
                    field.setPadding(0,nodeWidth/2,0,nodeWidth/2);
                } else {
                    field.setPadding(0,(layout.getLayoutParams().height-(nodeWidth/2)*((lvlCount*2)-1))/2,0,(layout.getLayoutParams().height-(nodeWidth/2)*((lvlCount*2)-1))/2);
                }
        }

//        TextView t = field.findViewById((int)element.get("id")).findViewWithTag("nodeName");
//        t.setText("сx: "+zoom.centerX+
//                    "\nсy: "+zoom.centerY);
//            ArrayList children = db.GetChildren((int)element.get("id"));
//            if (children.size() > 0){
//                if (children.size() == 1){
//                    ArrayMap child = (ArrayMap) children.get(0);
//                    set.connect((int)child.get("id"), ConstraintSet.LEFT, (int)element.get("id"), ConstraintSet.LEFT, 0);
//                    set.connect((int)child.get("id"), ConstraintSet.RIGHT, (int)element.get("id"), ConstraintSet.RIGHT, 0);
//                    set.connect((int)child.get("id"), ConstraintSet.TOP, (int)element.get("id"), ConstraintSet.BOTTOM, nodeWidth/2);
//                }else{
//                    int[] chainViews = new int[children.size()];
//                    float[] weights = new float[children.size()];
//                    for (int k = 0; k < children.size(); k++){
//                        ArrayMap child = (ArrayMap)children.get(k);
//                        chainViews[k] = (int)child.get("id");
//                        weights[k] = 0;
//                        set.connect((int)child.get("id"), ConstraintSet.TOP, (int)element.get("id"), ConstraintSet.BOTTOM, nodeWidth/2);
////                        int currentLevel = (int)childLevels.get(child.get("id"));
////                        int maxElementCount = (int)lvlElementCount.get(currentLevel);
////                        for (int j = currentLevel; j < lvlElementCount.size(); j++){
////                            if (maxElementCount < (int)lvlElementCount.get(j)){maxElementCount = (int)lvlElementCount.get(j);}
////                        }
////                        int nodeMargin = ((maxElementCount*nodeWidth*2) - (nodeWidth*(int)lvlElementCount.get(currentLevel)))/(int)lvlElementCount.get(currentLevel)/2;
////
////                        ArrayList allChildrenList = db.GetAllChildrenList((int)child.get("id"));
////                        ArrayList lvls = new ArrayList();
////                        for (int j = 0; j < lvlCount; j++){
////                            lvls.add(0);
////                        }
////                        for (int j = 1; j < allChildrenList.size(); j++){
////                            ArrayMap takedChildren = (ArrayMap) allChildrenList.get(j);
////                            int count = (int)lvls.get((int)childLevels.get(takedChildren.get("id")));
////                            lvls.set((int)childLevels.get(takedChildren.get("id")), count+1);
////                        }
////                        int max = 0;
////                        for (int j = 0; j < lvls.size(); j++){
////                            if (max < (int)lvls.get(j)){max = (int)lvls.get(j);}
////                            if (max == 1){max = 0;}
////                        }
////
////                        int nodeMargin = (nodeWidth*max)+(nodeWidth*max)/2-nodeWidth/2;
////                        if (max <= 1) {nodeMargin = nodeWidth/2;}
////                        if (k == 0){
////                            set.connect((int)child.get("id"), ConstraintSet.LEFT, (int)element.get("id"), ConstraintSet.LEFT, 0);
////                            set.connect((int)child.get("id"), ConstraintSet.TOP, (int)element.get("id"), ConstraintSet.BOTTOM, nodeWidth/2);
////                        }else{
////                            ArrayMap prev = (ArrayMap)children.get(k-1);
////                            set.connect((int)child.get("id"), ConstraintSet.LEFT, (int)prev.get("id"), ConstraintSet.RIGHT, nodeWidth/2);
////                            set.connect((int)child.get("id"), ConstraintSet.TOP, (int)prev.get("id"), ConstraintSet.TOP, 0);
////                            if (k == children.size()-1){
////                                set.connect((int)child.get("id"), ConstraintSet.RIGHT, (int)element.get("id"), ConstraintSet.RIGHT, 0);
////                            }
////                        }
////                        chainViews[k] = (int)child.get("id");
////                        weights[k] = 1;
//                        set.setMargin((int)child.get("id"), ConstraintSet.START, nodeMargin/2);
//                        set.setMargin((int)child.get("id"), ConstraintSet.END, nodeMargin/2);
//                    }
//                    set.createHorizontalChain((int)element.get("id"), ConstraintSet.LEFT, (int)element.get("id"), ConstraintSet.RIGHT,
//                                                chainViews, weights, ConstraintSet.CHAIN_PACKED);
//                }
//            }

        //Позиционирование элементов
        widthOfElements = new ArrayMap();
        for (int i = elementsOfLevels.size()-1; i > -1; i--){
            ArrayList currentLevel = (ArrayList) elementsOfLevels.get(i);
            for (int k = 0; k < currentLevel.size(); k++){
                int elementId = (int)currentLevel.get(k);
                ArrayList elementChildren = db.GetChildren(elementId);
                if (elementChildren.size() == 0){
                    widthOfElements.put(elementId, nodeWidth*1.5);
                    set.setMargin(elementId, ConstraintSet.START, nodeWidth/4);
                    set.setMargin(elementId, ConstraintSet.END, nodeWidth/4);
                } else {
                    double elementWidth = 0;
                    int[] childrenId = new int[elementChildren.size()];
                    float[] childrenWeights = new float[elementChildren.size()];
                    for (int j = 0; j < elementChildren.size(); j++){
                        int childId = (int)((ArrayMap)elementChildren.get(j)).get("id");
                        elementWidth += (double)widthOfElements.get(childId);
                        childrenId[j] = childId;
                        childrenWeights[j] = 1;
                        if (j == 0){
                            set.connect(childId, ConstraintSet.TOP, elementId, ConstraintSet.BOTTOM, nodeWidth/2);
                        }else{
                            set.connect(childId, ConstraintSet.TOP, (int)((ArrayMap)elementChildren.get(j-1)).get("id"), ConstraintSet.TOP);
                        }
                    }
                    set.setMargin(elementId, ConstraintSet.START, ((int)Math.round(elementWidth)-nodeWidth)/2);
                    set.setMargin(elementId, ConstraintSet.END, ((int)Math.round(elementWidth)-nodeWidth)/2);
                    widthOfElements.put(elementId, elementWidth);
                    if (elementChildren.size() > 1){set.createHorizontalChain(elementId, ConstraintSet.LEFT, elementId, ConstraintSet.RIGHT, childrenId, childrenWeights, ConstraintSet.CHAIN_PACKED);}
                    else {
                        set.connect((int)((ArrayMap)elementChildren.get(0)).get("id"), ConstraintSet.LEFT, elementId, ConstraintSet.LEFT);
                        set.connect((int)((ArrayMap)elementChildren.get(0)).get("id"), ConstraintSet.RIGHT, elementId, ConstraintSet.RIGHT);
                    }
                }
            }
        }


        LinedBackground backGround = new LinedBackground(childList, field, context, nodeWidth);
        field.setBackground(backGround);
        field.invalidate();
        field.requestLayout();
        set.applyTo(field);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    private View getNode(int id){
        Node nodeGen = new Node(context, id, fm);
        LinearLayout node = (LinearLayout) nodeGen.getNode();
        node.setId(id);//(View.generateViewId());

        return node;
    }

    public void setSizeChangeListener(){
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int oldWidth = oldRight - oldLeft;
                int width = right - left;
                int oldHeight = oldBottom - oldTop;
                int height = bottom - top;
                if (oldWidth != width){
                    setConstraints();
                }
                if (oldHeight !=  height){
                    setConstraints();
                }
            }
        });
    }
}
