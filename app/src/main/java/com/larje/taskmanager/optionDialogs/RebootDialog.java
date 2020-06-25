package com.larje.taskmanager.optionDialogs;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.OptionsActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.SplashActivity;
import com.larje.taskmanager.data.DBManager;

public class RebootDialog extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        getDialog().setTitle("lang");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);

        final View v = inflater.inflate(R.layout.reboot_dialog, null);
        v.findViewById(R.id.reboot_dialog_root).setBackgroundResource(R.drawable.rounded_layout);

        return v;
    }
}
