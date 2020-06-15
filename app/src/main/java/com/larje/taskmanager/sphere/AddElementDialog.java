package com.larje.taskmanager.sphere;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.customview.ColorPicker;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

import java.util.Random;

public class AddElementDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private int parrent;
    private int root;
    private boolean clrLineIsOpen = false;
    private DBManager db;
    private Dialog dlg;
    int r = 0;
    int g = 0;
    int b = 0;

    public AddElementDialog(int parrent, int root){
        this.parrent = parrent;
        this.root = root;
    }

    public AddElementDialog(int parrent, int root, Dialog dlg){
        this.parrent = parrent;
        this.root = root;
        this.dlg = dlg;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    public void fullHeightAnimation(final View v, final String tag){
        ValueAnimator fullHeightAnim = ValueAnimator.ofInt(v.findViewWithTag(tag).getMeasuredHeight(), v.getMeasuredHeight());
        fullHeightAnim.setDuration(70);
        fullHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.findViewWithTag(tag).getLayoutParams().height = (int)animation.getAnimatedValue();
            }
        });
        fullHeightAnim.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        this.db = new DBManager(getContext());
        getDialog().setTitle("Новый Элемент");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.add_element_dialog_sphere, null);
        v.findViewById(R.id.add_element_dialog_sphere_root).setBackgroundResource(R.drawable.rounded_layout);


        final View clrLine = v.findViewById(R.id.color_line);
        clrLine.setVisibility(View.GONE);
        final View clrPicker = v.findViewById(R.id.colorPicker);
        final View clrPickerPanel = v.findViewById(R.id.colorPickerPanel);
        switch (root){
            case (0):
                //sphere
                Random rand = new Random();
                this.r = 100+rand.nextInt(155);
                this.g = 100+rand.nextInt(155);
                this.b = 100+rand.nextInt(155);
                clrLine.setBackgroundColor(Color.rgb(r,g,b));
                clrPicker.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                break;
                            case MotionEvent.ACTION_MOVE:
                                ArrayMap clr = ColorPicker.GetColor(v.getWidth(), (int)event.getX());
                                r = (int)clr.get("R");
                                g = (int)clr.get("G");
                                b = (int)clr.get("B");
                                clrLine.setBackgroundColor(Color.rgb(r, g, b));
                                clrPickerPanel.findViewWithTag("open").setBackgroundColor(Color.rgb(r, g, b));
                                break;
                        }
                        return true;
                    }
                });
                final String[] clrs = new String[] {"EF9A9A", "B39DDB", "81D4FA", "A5D6A7", "FFF59D", "FFAB91"};
                for (int i = 0; i < 6; i++){
                    Button btn = clrPickerPanel.findViewWithTag(""+i);
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            clrLine.setBackgroundColor(Color.parseColor("#"+clrs[finalI]));
                            final ViewGroup.LayoutParams newParams = v.getLayoutParams();
                            ValueAnimator onAnim = ValueAnimator.ofInt(v.getMeasuredHeight(), clrPickerPanel.getMeasuredHeight()/2);
                            onAnim.setDuration(70);
                            onAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    newParams.height = (int)animation.getAnimatedValue();
                                    v.setLayoutParams(newParams);
                                }
                            });
                            onAnim.start();
                            r = Color.red(Color.parseColor("#"+clrs[finalI]));
                            g = Color.green(Color.parseColor("#"+clrs[finalI]));
                            b = Color.blue(Color.parseColor("#"+clrs[finalI]));

                            for (int k = 0; k < 6; k++){
                                if (k != finalI){
                                    final ViewGroup.LayoutParams oldParams = clrPickerPanel.findViewWithTag(""+k).getLayoutParams();
                                    ValueAnimator offAnim = ValueAnimator.ofInt(clrPickerPanel.findViewWithTag(""+k).getMeasuredHeight(), clrPickerPanel.getMeasuredHeight());
                                    offAnim.setDuration(70);
                                    final int finalK = k;
                                    offAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            oldParams.height = (int)animation.getAnimatedValue();
                                            clrPickerPanel.findViewWithTag(""+ finalK).setLayoutParams(oldParams);
                                        }
                                    });
                                    offAnim.start();
                                }
                            }
                            fullHeightAnimation(clrPickerPanel, "open");
                        }
                    });
                }

                Button btn = clrPickerPanel.findViewWithTag("open");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (clrLineIsOpen){
                            for(int i = 0; i < 6; i++){
                                clrPickerPanel.findViewWithTag(""+i).setVisibility(View.VISIBLE);
                                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clrPickerPanel.findViewWithTag(""+i).getLayoutParams();
                                ValueAnimator fullMarginAnim = ValueAnimator.ofInt(0, ((LinearLayout.LayoutParams)(clrPickerPanel.findViewWithTag("5").getLayoutParams())).rightMargin);
                                fullMarginAnim.setDuration(150);
                                final int finalI = i;
                                fullMarginAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        params.rightMargin = (int)animation.getAnimatedValue();
                                        clrPickerPanel.findViewWithTag(""+ finalI).setLayoutParams(params);
                                    }
                                });
                                clrPicker.setVisibility(View.GONE);
                                fullMarginAnim.start();
                            }
                            ValueAnimator halfHeightAnim = ValueAnimator.ofInt(clrPickerPanel.findViewWithTag("open").getMeasuredHeight(),clrPickerPanel.findViewWithTag("open").getMeasuredHeight()/2);
                            halfHeightAnim.setDuration(70);
                            halfHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    clrPickerPanel.findViewWithTag("open").getLayoutParams().height = (int)animation.getAnimatedValue();
                                }
                            });
                            halfHeightAnim.start();
                            clrLineIsOpen = false;
                        }else{
                            for(int i = 0; i < 6; i++){
                                final int finalI = i;
                                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clrPickerPanel.findViewWithTag(""+i).getLayoutParams();
                                ValueAnimator fullHeightAnim = ValueAnimator.ofInt(clrPickerPanel.findViewWithTag(""+i).getMeasuredHeight(), clrPickerPanel.getMeasuredHeight());
                                fullHeightAnim.setDuration(70);
                                final ValueAnimator zeroMarginAnim = ValueAnimator.ofInt(params.rightMargin, 0);
                                zeroMarginAnim.setDuration(150);
                                zeroMarginAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        params.rightMargin = (int)animation.getAnimatedValue();
                                        clrPickerPanel.findViewWithTag(""+finalI).setLayoutParams(params);
                                        if ((int)animation.getAnimatedValue() == 0){
                                            for (int k = 0; k < 6; k++){
                                                clrPickerPanel.findViewWithTag(""+k).setVisibility(View.GONE);
                                            }
                                            clrPicker.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                fullHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        params.height = (int)animation.getAnimatedValue();
                                        clrPickerPanel.findViewWithTag(""+ finalI).setLayoutParams(params);
                                        if (((int)animation.getAnimatedValue() == clrPickerPanel.getMeasuredHeight() & (finalI != 5))){
                                            zeroMarginAnim.start();
                                        }
                                    }
                                });
                                fullHeightAnim.start();
                                fullHeightAnimation(clrPickerPanel, "open");
                            }
                            ((Button)clrPickerPanel.findViewWithTag("open")).setText("");
                            clrLineIsOpen = true;
                        }
                    }
                });

                break;
            case (1):
                //kategory
                this.r = Integer.parseInt(db.GetElement(parrent).get("color").toString().split(":")[0]);
                this.g = Integer.parseInt(db.GetElement(parrent).get("color").toString().split(":")[1]);
                this.b = Integer.parseInt(db.GetElement(parrent).get("color").toString().split(":")[2]);
                clrPickerPanel.setVisibility(View.GONE);
                clrLine.setVisibility(View.GONE);
                break;
        }



        final EditText edittxt = v.findViewById(R.id.sphere_name_edittext);
        Button ok = v.findViewById(R.id.ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittxt.getText().length() != 0){
                    db.PutElement(edittxt.getText().toString(), parrent, ""+r+":"+g+":"+b);
                    db.CheckElementStatus(parrent);
                    dismiss();
                    PlaceholderFragment.sphereAnim = false;
                    MainActivity.view.getAdapter().notifyDataSetChanged();
                    if (dlg != null){
                        dlg.dismiss();
                        PlaceholderFragment.spherelvl = 1;
                    }
                }
            }});

        Button cancel = v.findViewById(R.id.cncl_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }});

        return v;
    }
}
