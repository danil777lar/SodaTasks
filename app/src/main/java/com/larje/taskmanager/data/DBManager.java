package com.larje.taskmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;
import android.util.Log;


import androidx.annotation.NonNull;

import com.larje.taskmanager.service.NotificationAlarm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DBManager{

    DBCreator dbCreator;
    SQLiteDatabase db;

    public DBManager(Context context){
        this.dbCreator = new DBCreator(context);
        this.db = dbCreator.getWritableDatabase();
    }

    public ArrayList GetChildren(int id) {
        ArrayList data = new ArrayList();
        Cursor c = this.db.query("element", null, "parent = "+id, null, null,null,null);
        if (c.moveToFirst()){
            do{
                ArrayMap element = new ArrayMap();
                element.put("id", c.getInt(c.getColumnIndex("id")));
                element.put("name", c.getString(c.getColumnIndex("name")));
                element.put("parent", c.getInt(c.getColumnIndex("parent")));
                element.put("color", c.getString(c.getColumnIndex("color")));
                element.put("description", c.getString(c.getColumnIndex("description")));
                element.put("status", c.getInt(c.getColumnIndex("status")));
                data.add(element);
            }while (c.moveToNext());
        }
        return data;
    }

    public ArrayList GetAllChildrenList(int id){
        ArrayList data = new ArrayList();
        Cursor c = this.db.query("element", null, "id = "+id, null, null,null,null);
        if (c.moveToFirst()){
            ArrayMap element = new ArrayMap();
            element.put("id", c.getInt(c.getColumnIndex("id")));
            element.put("name", c.getString(c.getColumnIndex("name")));
            element.put("parent", c.getInt(c.getColumnIndex("parent")));
            element.put("color", c.getString(c.getColumnIndex("color")));
            element.put("description", c.getString(c.getColumnIndex("description")));
            element.put("status", c.getInt(c.getColumnIndex("status")));
            data.add(element);
        }
        c = this.db.query("element", null, "parent = "+id, null, null,null,null);
        if (c.moveToFirst()){
            do{
                ArrayList underData = GetAllChildrenList(c.getInt(c.getColumnIndex("id")));
                for (int i = 0; i < underData.size(); i++){
                    data.add(underData.get(i));
                }
            }while (c.moveToNext());
        }
        return data;
    }

    @NonNull
    public ArrayMap GetElement(int id){
        ArrayMap element = new ArrayMap();
        Cursor c = this.db.query("element", null, "id = "+id, null, null,null,null);
        if(c.moveToFirst()){
            element.put("id", c.getInt(c.getColumnIndex("id")));
            element.put("name", c.getString(c.getColumnIndex("name")));
            element.put("parent", c.getInt(c.getColumnIndex("parent")));
            element.put("color", c.getString(c.getColumnIndex("color")));
            element.put("description", c.getString(c.getColumnIndex("description")));
            element.put("status", c.getInt(c.getColumnIndex("status")));
        }
        return element;
    }

    public void PutElement(String name, int parrent, String color){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("parent", parrent);
        cv.put("color", color);
        cv.put("description", "");
        cv.put("status", 0);
        long row = this.db.insert("element", null, cv);
    }

    public void DeleteElement(int id){
        ArrayList children = GetChildren(id);
        db.delete("element", "id = "+id, null);
        this.DeleteTask(id);
        for (int i = 0; i < children.size(); i++){
            ArrayMap element = (ArrayMap) children.get((int)i);
            DeleteElement((int) element.get("id"));
        }
    }

    public void UpdateElement(String name, int id, String color){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("color", color);
        long row = this.db.update("element", cv, "id = "+id, null);
    }

    public void UpdateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        long row = this.db.update("element", cv, "id = "+id, null);
    }

    public void UpdateDescription(int id, String description){
        ContentValues cv = new ContentValues();
        cv.put("description", description);
        long row = this.db.update("element", cv, "id = "+id, null);
    }

    public boolean CheckTask(int elementId){
        boolean task = false;
        Cursor c = this.db.query("task", null, "element = "+elementId, null, null,null,null);
        if (c.moveToFirst()){
           task = true;
        }
        return task;
    }

    public void PutTask(int elementId, String date, String time, int type){
        ContentValues cv = new ContentValues();
        cv.put("element", elementId);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("type", type);
        long row = this.db.insert("task", null, cv);
    }

    public void UpdateTask(int elementId, String date, String time, int type){
        ContentValues cv = new ContentValues();
        cv.put("element", elementId);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("type", type);
        long row = this.db.update("task", cv, "element = "+elementId, null);
    }

    public ArrayMap GetTask(int elementId){
        ArrayMap task = new ArrayMap();
        Cursor c = this.db.query("task", null, "element = "+elementId, null, null,null,null);
        if(c.moveToFirst()){
            task.put("id", c.getInt(c.getColumnIndex("id")));
            task.put("element", c.getInt(c.getColumnIndex("element")));
            task.put("date", c.getString(c.getColumnIndex("date")));
            task.put("time", c.getString(c.getColumnIndex("time")));
            task.put("type", c.getInt(c.getColumnIndex("type")));
        }
        return task;
    }

    public void DeleteTask(int elementId){
        Cursor c = db.query("task", null,"element = "+elementId, null, null, null, null);
        int taskId = 0;
        if (c.moveToFirst()){
            taskId = c.getInt(c.getColumnIndex("id"));
        }
        db.delete("task", "element = "+elementId, null);
        db.delete("repeattask", "task = "+taskId, null);
    }

    public void CheckElementStatus(int elementId){
        ArrayList children = GetChildren(elementId);
        boolean done = true;
        for (int i = 0; i < children.size(); i++){
            ArrayMap child = (ArrayMap)children.get(i);
            if ((int)child.get("status") == 0){
                done = false;
            }
        }
        ContentValues cv = new ContentValues();
        if (done){
            cv.put("status", 1);
        }else{
            cv.put("status", 0);
        }
        long row = this.db.update("element", cv, "id = "+elementId, null);
    }

    public ArrayList GetTaskDates(){
        ArrayList dates = new ArrayList();
        ArrayMap settings = GetSettings();
        Cursor c = this.db.query("task", null, null, null, "date", null, null);
        Calendar calendar = new GregorianCalendar();
        Calendar currentCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+(int)settings.get("taskdays"));
        for (int i = 0; i < (int)settings.get("taskdays"); i++){
            ArrayMap date = new ArrayMap();
            date.put("day", ""+(calendar.get(Calendar.DAY_OF_MONTH)+i));
            date.put("month", ""+calendar.get(Calendar.MONTH));
            date.put("year", ""+calendar.get(Calendar.YEAR));
            dates.add(date);
        }
        if (c.moveToFirst()){
            do{
                ArrayMap date = new ArrayMap();
                String[] d = c.getString(c.getColumnIndex("date")).split("/");
                if (d.length > 1){
                    Calendar dataCalendar = new GregorianCalendar(Integer.parseInt(d[2]), Integer.parseInt(d[1]), Integer.parseInt(d[0]));
                    if (dataCalendar.getTimeInMillis() >= currentCalendar.getTimeInMillis()){
                        date.put("day", d[0]);
                        date.put("month", d[1]);
                        date.put("year", d[2]);
                        dates.add(date);
                    }
                }
            } while (c.moveToNext());
        }

        Boolean isSorted = false;
        while (!isSorted) {
            isSorted = true;
            for (int i = 0; i < dates.size() - 1; i++) {
                ArrayMap currentDate = (ArrayMap) dates.get(i);
                ArrayMap nextDate = (ArrayMap) dates.get(i + 1);
                if (Integer.parseInt((String) currentDate.get("year")) > Integer.parseInt((String) nextDate.get("year"))) {
                    dates.set(i, nextDate);
                    dates.set(i + 1, currentDate);
                    isSorted = false;
                } else if (Integer.parseInt((String) currentDate.get("year")) == Integer.parseInt((String) nextDate.get("year"))) {
                    if (Integer.parseInt((String) currentDate.get("month")) > Integer.parseInt((String) nextDate.get("month"))) {
                        dates.set(i, nextDate);
                        dates.set(i + 1, currentDate);
                        isSorted = false;
                    } else if (Integer.parseInt((String) currentDate.get("month")) == Integer.parseInt((String) nextDate.get("month"))) {
                        if (Integer.parseInt((String) currentDate.get("day")) > Integer.parseInt((String) nextDate.get("day"))) {
                            dates.set(i, nextDate);
                            dates.set(i + 1, currentDate);
                            isSorted = false;
                        }
                    }
                }
            }
        }
        return dates;
    }

    public ArrayList GetTasksByDate(String date){
        ArrayList tasks = new ArrayList();
        Cursor c = this.db.query("task", null, "date = '"+date+"'", null, null,null, null);
        if (c.moveToFirst()){
            do{
                ArrayMap element = new ArrayMap();
                element.put("id", c.getInt(c.getColumnIndex("id")));
                element.put("element", c.getInt(c.getColumnIndex("element")));
                element.put("date", c.getString(c.getColumnIndex("date")));
                element.put("time", c.getString(c.getColumnIndex("time")));
                element.put("type", c.getInt(c.getColumnIndex("type")));
                tasks.add(element);
            }while (c.moveToNext());
        }

        Cursor d = this.db.query("task", null, "type = 2", null, null,null, null);
        Calendar cal = new GregorianCalendar(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[1]), Integer.parseInt(date.split("/")[0]));
        String currentWeekDay = cal.getTime().toString().split(" ")[0];
        ArrayMap weekDays = new ArrayMap();
        weekDays.put("Mon", '0');
        weekDays.put("Tue", '1');
        weekDays.put("Wed", '2');
        weekDays.put("Thu", '3');
        weekDays.put("Fri", '4');
        weekDays.put("Sat", '5');
        weekDays.put("Sun", '6');
        if (d.moveToFirst()){
            do{
                if (d.getString(d.getColumnIndex("date")).indexOf((char)weekDays.get(currentWeekDay)) != -1){
                    ArrayMap element = new ArrayMap();
                    element.put("id", d.getInt(d.getColumnIndex("id")));
                    element.put("element", d.getInt(d.getColumnIndex("element")));
                    element.put("date", d.getString(d.getColumnIndex("date")));
                    element.put("time", d.getString(d.getColumnIndex("time")));
                    element.put("type", d.getInt(d.getColumnIndex("type")));
                    tasks.add(element);
                }
            }while (d.moveToNext());
        }

        boolean isSorted = false;
        while (!isSorted){
            isSorted = true;
            for (int i = 0; i < tasks.size()-1; i++){
                ArrayMap currentTask = (ArrayMap)tasks.get(i);
                ArrayMap nextTask = (ArrayMap)tasks.get(i+1);
                int currentHour = Integer.parseInt(currentTask.get("time").toString().split("/")[0]);
                int currentMinute = Integer.parseInt(currentTask.get("time").toString().split("/")[1]);
                int nextHour = Integer.parseInt(nextTask.get("time").toString().split("/")[0]);
                int nextMinute = Integer.parseInt(nextTask.get("time").toString().split("/")[1]);
                if (currentHour > nextHour){
                    tasks.set(i, nextTask);
                    tasks.set(i+1, currentTask);
                    isSorted = false;
                } else if (currentHour == nextHour){
                    if (currentMinute > nextMinute){
                        tasks.set(i, nextTask);
                        tasks.set(i+1, currentTask);
                        isSorted = false;
                    }
                }
            }
        }

        return tasks;
    }

    public int GetRepeatTask(String date, int task){
        int status = 0;
        Cursor c = this.db.query("repeattask", null, "(date = '"+date+"') and (task = "+task+")", null, null, null, null);
        if (c.moveToFirst()){
            status = c.getInt(c.getColumnIndex("status"));
        } else {
            ContentValues cv = new ContentValues();
            cv.put("task", task);
            cv.put("date", date);
            cv.put("status", 0);
            long row = this.db.insert("repeattask", null, cv);
        }
        return  status;
    }

    public void UpdateRepeatTask(String date, int task, int status){
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        long row = this.db.update("repeattask", cv, "(date = '"+date+"') and (task = "+task+")", null);

    }

    public ArrayList GetTaskKategories(int kategoryId){
        ArrayList data = new ArrayList();

        ArrayList elements = GetAllChildrenList(kategoryId);
        for (int i = 0; i < elements.size(); i++){
            ArrayMap task = GetTask((int)((ArrayMap)elements.get(i)).get("id"));
            if (!task.isEmpty()){
                if ((int)task.get("type") != 2){
                    data.add(task);
                }
            }
        }
        return bubbleSort(data);
    }

    public ArrayList GetDeadlines (){
        ArrayList data = new ArrayList();
        Cursor c = this.db.query("task", null, "type = 1", null, null,null,null);
        if (c.moveToFirst()){
            do{
                ArrayMap task = new ArrayMap();
                task.put("id", c.getInt(c.getColumnIndex("id")));
                task.put("element", c.getInt(c.getColumnIndex("element")));
                task.put("date", c.getString(c.getColumnIndex("date")));
                task.put("time", c.getString(c.getColumnIndex("time")));
                task.put("type", c.getInt(c.getColumnIndex("type")));
                data.add(task);
            }while (c.moveToNext());
        }

        boolean isSorted = false;
        while (!isSorted){
            isSorted = true;
            for (int i = 0; i < data.size()-1; i++){
                ArrayMap currentTask = (ArrayMap)data.get(i);
                ArrayMap nextTask = (ArrayMap)data.get(i+1);
                int currentHour = Integer.parseInt(currentTask.get("time").toString().split("/")[0]);
                int currentMinute = Integer.parseInt(currentTask.get("time").toString().split("/")[1]);
                int currentDay = Integer.parseInt(currentTask.get("date").toString().split("/")[0]);
                int currentMonth = Integer.parseInt(currentTask.get("date").toString().split("/")[1]);
                int currentYear = Integer.parseInt(currentTask.get("date").toString().split("/")[2]);

                int nextHour = Integer.parseInt(nextTask.get("time").toString().split("/")[0]);
                int nextMinute = Integer.parseInt(nextTask.get("time").toString().split("/")[1]);
                int nextDay = Integer.parseInt(nextTask.get("date").toString().split("/")[0]);
                int nextMonth = Integer.parseInt(nextTask.get("date").toString().split("/")[1]);
                int nextYear = Integer.parseInt(nextTask.get("date").toString().split("/")[2]);

                if (currentYear > nextYear){
                    data.set(i, nextTask);
                    data.set(i+1, currentTask);
                    isSorted = false;
                } else if (currentYear == nextYear){
                    if (currentMonth > nextMonth){
                        data.set(i, nextTask);
                        data.set(i+1, currentTask);
                        isSorted = false;
                    } else if (currentMonth == nextMonth){
                        if (currentDay > nextDay){
                            data.set(i, nextTask);
                            data.set(i+1, currentTask);
                            isSorted = false;
                        } else if (currentDay == nextDay){
                            if (currentHour > nextHour){
                                data.set(i, nextTask);
                                data.set(i+1, currentTask);
                                isSorted = false;
                            } else if (currentHour == nextHour){
                                if (currentMinute > nextMinute){
                                    data.set(i, nextTask);
                                    data.set(i+1, currentTask);
                                    isSorted = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return data;
    }

    public ArrayMap GetSettings(){
        ArrayMap settings = new ArrayMap();
        Cursor c = this.db.query("settings", null, "id = 1", null, null,null,null);
        if(c.moveToFirst()){
            settings.put("id", c.getInt(c.getColumnIndex("id")));
            settings.put("theme", c.getInt(c.getColumnIndex("theme")));
            settings.put("language", c.getString(c.getColumnIndex("language")));
            settings.put("taskdays", c.getInt(c.getColumnIndex("taskdays")));
            settings.put("notswitch", c.getInt(c.getColumnIndex("notswitch")));
            settings.put("nottime", c.getInt(c.getColumnIndex("nottime")));
        }
        c.close();
        return settings;
    }

    public void UpdateSettings(ArrayMap data){
        ContentValues cv = new ContentValues();
        cv.put("theme", (int)data.get("theme"));
        cv.put("language", (String)data.get("language"));
        cv.put("taskdays", (int)data.get("taskdays"));
        cv.put("notswitch", (int)data.get("notswitch"));
        cv.put("nottime", (int)data.get("nottime"));
        long row = this.db.update("settings", cv, "id = 1", null);

    }

    public ArrayMap GetNotificationTasks(){
        ArrayList data = new ArrayList();
        Cursor c = this.db.query("task", null, "type = 0", null, null,null,null);
        if (c.moveToFirst()){
            do{
                ArrayMap task = new ArrayMap();
                task.put("element", c.getInt(c.getColumnIndex("element")));
                task.put("date", c.getString(c.getColumnIndex("date")));
                task.put("time", c.getString(c.getColumnIndex("time")));
                data.add(task);
            }while (c.moveToNext());
        }
        c = this.db.query("repeattask", null, null, null, null,null,null);
        if (c.moveToFirst()){
            do{
                Cursor k = this.db.query("task", null, "id = "+c.getInt(c.getColumnIndex("task")), null, null,null,null);
                if (k.moveToFirst()){
                    ArrayMap task = new ArrayMap();
                    task.put("element", k.getInt(k.getColumnIndex("element")));
                    task.put("date", c.getString(c.getColumnIndex("date")));
                    task.put("time", k.getString(k.getColumnIndex("time")));
                    data.add(task);
                }
                k.close();
            }while (c.moveToNext());
        }

        data = bubbleSort(data);
        ArrayMap actualData = new ArrayMap();

        int i = 0;
        while ((actualData.isEmpty()) & (i < data.size())){
            String[] date = ((ArrayMap)data.get(i)).get("date").toString().split("/");
            String[] time = ((ArrayMap)data.get(i)).get("time").toString().split("/");

            Calendar currentTime = new GregorianCalendar();
            Calendar dataTime = new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);

            if (currentTime.getTimeInMillis() < dataTime.getTimeInMillis()){
                actualData = (ArrayMap)data.get(i);
            }

            i++;
        }


        c.close();
        return actualData;
    }

    private ArrayList bubbleSort(ArrayList data){
        boolean isSorted = false;
        while (!isSorted){
            isSorted = true;
            for (int i = 0; i < data.size()-1; i++){
                ArrayMap currentTask = (ArrayMap)data.get(i);
                ArrayMap nextTask = (ArrayMap)data.get(i+1);
                int currentHour = Integer.parseInt(currentTask.get("time").toString().split("/")[0]);
                int currentMinute = Integer.parseInt(currentTask.get("time").toString().split("/")[1]);
                int currentDay = Integer.parseInt(currentTask.get("date").toString().split("/")[0]);
                int currentMonth = Integer.parseInt(currentTask.get("date").toString().split("/")[1]);
                int currentYear = Integer.parseInt(currentTask.get("date").toString().split("/")[2]);

                int nextHour = Integer.parseInt(nextTask.get("time").toString().split("/")[0]);
                int nextMinute = Integer.parseInt(nextTask.get("time").toString().split("/")[1]);
                int nextDay = Integer.parseInt(nextTask.get("date").toString().split("/")[0]);
                int nextMonth = Integer.parseInt(nextTask.get("date").toString().split("/")[1]);
                int nextYear = Integer.parseInt(nextTask.get("date").toString().split("/")[2]);

                if (currentYear > nextYear){
                    data.set(i, nextTask);
                    data.set(i+1, currentTask);
                    isSorted = false;
                } else if (currentYear == nextYear){
                    if (currentMonth > nextMonth){
                        data.set(i, nextTask);
                        data.set(i+1, currentTask);
                        isSorted = false;
                    } else if (currentMonth == nextMonth){
                        if (currentDay > nextDay){
                            data.set(i, nextTask);
                            data.set(i+1, currentTask);
                            isSorted = false;
                        } else if (currentDay == nextDay){
                            if (currentHour > nextHour){
                                data.set(i, nextTask);
                                data.set(i+1, currentTask);
                                isSorted = false;
                            } else if (currentHour == nextHour){
                                if (currentMinute > nextMinute){
                                    data.set(i, nextTask);
                                    data.set(i+1, currentTask);
                                    isSorted = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return data;
    }

}


