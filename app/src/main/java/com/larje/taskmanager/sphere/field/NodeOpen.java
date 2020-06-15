package com.larje.taskmanager.sphere.field;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.customview.ColorPicker;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.sphere.AddElementDialog;
import com.larje.taskmanager.sphere.DeleteElementDialog;
import com.larje.taskmanager.task.AddTaskDialog;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

import java.util.ArrayList;
import java.util.Random;

public class NodeOpen extends DialogFragment implements DialogInterface.OnClickListener {

    private int elementId;
    private ArrayMap element;
    private EditText description;
    DBManager db;

    public NodeOpen(int elementId){
        this.elementId = elementId;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        this.db = new DBManager(getContext());
        this.element = db.GetElement(elementId);

        getDialog().setTitle("Новый Элемент");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.node_open, null);
        v.findViewById(R.id.node_open_root).setBackgroundResource(R.drawable.rounded_layout);
        TextView title = v.findViewById(R.id.node_open_name);
        title.setText((String) db.GetElement(elementId).get("name"));
        title.setTypeface(MainActivity.face);
        TextView description_title = v.findViewById(R.id.node_open_description_title);

        description_title.setTypeface(MainActivity.face);
        this.description = v.findViewById(R.id.node_open_description_field);
        description.setText(db.GetElement(elementId).get("description").toString());
        description.setTypeface(MainActivity.face);

        makeChekBtn(v);
        makeDeleteBtn(v);
        makeNewBtn(v);
        makeTaskBtn(v);

        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        db.UpdateDescription(elementId, description.getText().toString());
        PlaceholderFragment.sphereAnim = false;
        MainActivity.view.getAdapter().notifyDataSetChanged();
    }

    private void makeChekBtn(View v){
        LinearLayout btn = v.findViewById(R.id.node_open_btn_chek);
        final ImageView img = v.findViewById(R.id.node_open_btn_chek_ic);
        final TextView txt = v.findViewById(R.id.node_open_btn_chek_txt);

        if ((int)element.get("status") == 1){
            img.setImageResource(R.drawable.ic_done);
            txt.setText(getContext().getString(R.string.done));
        }
        else if ((int)element.get("status") == 0){
            txt.setText(getContext().getString(R.string.no_done));
            img.setImageResource(R.drawable.ic_nodone);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) element.get("status") == 0) {
                    db.UpdateStatus(elementId, 1);
                    element.put("status", 1);
                    txt.setText(getContext().getString(R.string.done));
                    img.setImageResource(R.drawable.ic_done);
                    ArrayList children = db.GetChildren((int)element.get("id"));
                    for (int i = 0; i < children.size(); i++){
                        ArrayMap child = (ArrayMap)children.get(i);
                        db.UpdateStatus((int)child.get("id"), 1);
                    }
                } else if ((int) element.get("status") == 1){
                    db.UpdateStatus(elementId, 0);
                    element.put("status", 0);
                    txt.setText(getContext().getString(R.string.no_done));
                    img.setImageResource(R.drawable.ic_nodone);
                }
                db.CheckElementStatus((int)element.get("parent"));
            }
        });
    }

    private void makeDeleteBtn(View v){
        LinearLayout btn = v.findViewById(R.id.node_open_btn_delete);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment deleteDialog = new DeleteElementDialog((elementId), getDialog());
                deleteDialog.show(getFragmentManager(), "deleteDialog");
            }
        });
    }

    private void makeNewBtn(View v){
        LinearLayout btn = v.findViewById(R.id.node_open_btn_new);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddElementDialog dialog = new AddElementDialog(elementId, 1, getDialog());
                dialog.show(getFragmentManager(), "add");
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void makeTaskBtn(View v){
        LinearLayout btn = v.findViewById(R.id.node_open_btn_task);
        if (db.CheckTask(elementId)){
            ((TextView)btn.findViewById(R.id.node_open_btn_task_txt)).setText(getContext().getString(R.string.edit_task));
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskDialog dialog = new AddTaskDialog(elementId, v);
                dialog.show(getFragmentManager(), "add"); }
        });
    }
}
