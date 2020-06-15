package com.larje.taskmanager.widgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class TodayTaskService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TodayTaskItemAdapter(getApplicationContext(), intent);
    }


}
