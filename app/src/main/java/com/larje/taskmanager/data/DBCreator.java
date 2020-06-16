package com.larje.taskmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.larje.taskmanager.MainActivity;

public class DBCreator  extends SQLiteOpenHelper {

    private Context context;

    public DBCreator(Context context){
        super(context, "tskmanagerdb", null, 9);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table element(id integer primary key autoincrement, name text, parent integer, color text, description text, status integer);");
        db.execSQL("create table file(id integer primary key autoincrement, element integer, path text);");
        db.execSQL("create table task(id integer primary key autoincrement, element integer, date text, time text, type integer);");
//        Task Types: (Оьычный - 0), (Дедлайн - 1), (Повтор - 2)
        db.execSQL("create table repeattask(id integer primary key autoincrement, task integer, date text, status integer);");
        db.execSQL("create table settings(id integer primary key autoincrement, theme integer, language text, taskdays integer, notswitch integer, nottime integer, appenternum integer);");
        setStandartSettings(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if((oldVersion == 1) & (newVersion == 2)){
            db.execSQL("create table repeattask(id integer primary key autoincrement, task integer, date text, status integer);");
        }
        if (oldVersion < 6){
            db.execSQL("create table settings(id integer primary key autoincrement, theme integer, language text, taskdays integer, notswitch integer, nottime integer);");
            setStandartSettings(db);
        }
        if (oldVersion < 9){
            db.execSQL("alter table settings add column appenternum;");
            setStandartSettings(db);
        }
    }

    private void setStandartSettings(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        String lang = context.getResources().getConfiguration().locale.getLanguage();

        if (lang != "ru"){ lang = "en"; }

        cv.put("theme", 1);
        cv.put("language", lang);
        cv.put("taskdays", 2);
        cv.put("notswitch", 1);
        cv.put("nottime", 15);
        cv.put("appenternum", 2);
        long row = db.insert("settings", null,  cv);
    }

}

