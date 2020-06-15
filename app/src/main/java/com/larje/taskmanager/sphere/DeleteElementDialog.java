package com.larje.taskmanager.sphere;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.ui.main.PlaceholderFragment;

public class DeleteElementDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private int elementId;
    private Dialog parrent;

    public DeleteElementDialog(int elementId, Dialog parrent){
        this.elementId = elementId;
        this.parrent = parrent;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        getDialog().setTitle("Новый Элемент");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.delete_element_dialog_sphere, null);
        v.findViewById(R.id.delete_element_dialog_sphere_root).setBackgroundResource(R.drawable.rounded_layout);


        Button cancel_btn = v.findViewById(R.id.delete_element_cancel);
        Button ok_btn = v.findViewById(R.id.delete_element_ok);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager db = new DBManager(getActivity().getApplicationContext());
                int parent = (int)db.GetElement(elementId).get("parent");
                db.DeleteElement(elementId);
                db.DeleteTask(elementId);
                db.CheckElementStatus(parent);
                dismiss();
                PlaceholderFragment.sphereAnim = false;
                MainActivity.view.getAdapter().notifyDataSetChanged();
                if (parrent.isShowing()){parrent.dismiss();}
            }
        });

        return v;
    }
}
