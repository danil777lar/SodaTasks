package com.larje.taskmanager.optionDialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.larje.taskmanager.OptionsActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.sphere.DeleteElementDialog;
import com.larje.taskmanager.sphere.EditElementDialog;

import java.util.Locale;

public class SettingsLanguageDialog extends DialogFragment implements DialogInterface.OnClickListener {

    DBManager db;
    TextView langTitle;
    Context baseContext;
    Context context;

    public SettingsLanguageDialog(TextView langTitle, Context context, Context baseContext){
        this.context = context;
        this.baseContext = baseContext;
        this.db = new DBManager(context);
        this.langTitle = langTitle;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstance){
        getDialog().setTitle("lang");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View v = inflater.inflate(R.layout.settings_language_dialog, null);
        v.findViewById(R.id.context_element_dialog_sphere_root).setBackgroundResource(R.drawable.rounded_layout);

        Button eng_btn = v.findViewById(R.id.btn_english);
        Button rus_btn = v.findViewById(R.id.btn_russian);

        eng_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                langTitle.setText("English");
                dismiss();
                ((OptionsActivity)context).updateLang("en");
            }
        });

        rus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                langTitle.setText("Русский");
                dismiss();
                ((OptionsActivity)context).updateLang("ru");
            }
        });

        return v;
    }
}
