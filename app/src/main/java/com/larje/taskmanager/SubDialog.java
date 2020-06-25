package com.larje.taskmanager;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.R;

public class SubDialog extends DialogFragment implements DialogInterface.OnClickListener {

    MyBilling myBilling;

    public SubDialog(MyBilling myBilling){
        this.myBilling = myBilling;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        getDialog().setTitle("lang");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.sub_dialog, null);
        v.findViewById(R.id.sub_dialog_root).setBackgroundResource(R.drawable.rounded_layout);

        Button later_btn = v.findViewById(R.id.sub_dialog_no);
        later_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button disable_btn = v.findViewById(R.id.sub_dialog_yes);
        disable_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBilling.makeSub();
            }
        });
        return v;
    }
}
