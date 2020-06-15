package com.larje.taskmanager.sphere;

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

import com.larje.taskmanager.R;

public class ContextElementDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private int elementId;
    private int root;

    public ContextElementDialog(int elementId, int root){
        this.elementId = elementId;
        this.root = root;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        getDialog().setTitle("Новый Элемент");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.context_element_dialog_sphere, null);
        v.findViewById(R.id.context_element_dialog_sphere_root).setBackgroundResource(R.drawable.rounded_layout);


        Button edit_btn = v.findViewById(R.id.edit_btn);
        Button dlt_btn = v.findViewById(R.id.delete_btn);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btns", "РЕДАКТИРОВАНИЕ "+elementId);
                DialogFragment editDialog = new EditElementDialog(elementId, root);
                editDialog.show(getFragmentManager(), "deleteDialog");
                dismiss();
            }
        });

        dlt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment deleteDialog = new DeleteElementDialog((elementId), getDialog());
                deleteDialog.show(getFragmentManager(), "deleteDialog");
                dismiss();
            }
        });
        return v;
    }
}
