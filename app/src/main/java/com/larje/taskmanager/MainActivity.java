package com.larje.taskmanager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.service.NotificationAlarm;
import com.larje.taskmanager.ui.main.PlaceholderFragment;
import com.larje.taskmanager.ui.main.SectionsPagerAdapter;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static ViewPager view;
    public static TabLayout tabs;
    public static Typeface face;
    public static LayoutInflater inflater;
    public static int spinnerResource;
    public static Intent mainToOptionsIntent;
    public static NotificationAlarm notificationAlarm;
    public static int theme;
    private int currentTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        setTheme();
        super.onCreate(savedInstanceState);

        DBManager db = new DBManager(this);
        final ArrayMap settings = db.GetSettings();
        settings.put("appenternum", (int)settings.get("appenternum")+1);
        db.UpdateSettings(settings);

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);

        this.view = viewPager;
        this.face = Typeface.createFromAsset(getAssets(), "18546.ttf");
        this.inflater = getLayoutInflater();
        this.spinnerResource = android.R.layout.simple_spinner_item;
        this.mainToOptionsIntent  = new Intent(MainActivity.this, OptionsActivity.class);
        this.notificationAlarm = new NotificationAlarm(this);

        viewPager.setAdapter(sectionsPagerAdapter);
        makeTabs(viewPager, settings);
        notificationAlarm.restart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLanguage();
    }

    @Override
    public void onBackPressed(){
        switch(tabs.getSelectedTabPosition()){
            case(0):
                if(PlaceholderFragment.spherelvl == 1){
                    PlaceholderFragment.spherelvl = 0;
                    PlaceholderFragment.sphereAnim = false;
                    view.getAdapter().notifyDataSetChanged();
                }else{super.onBackPressed();}
                break;
            case(1):
                super.onBackPressed();
                break;
            case(2):
                super.onBackPressed();
                break;
        }
    }

    private void setTheme(){
        DBManager db = new DBManager(this);
        final ArrayMap settings = db.GetSettings();
        theme = (int)settings.get("theme");
        switch(theme){
            case(0):
                setTheme(R.style.AppThemeLight);
                break;
            case(1):
                setTheme(R.style.AppThemeDark);
                break;
        }
    }

    public void setLanguage(){
        DBManager db = new DBManager(MainActivity.this);
        ArrayMap settings = db.GetSettings();
        String lang = (String)settings.get("language");
        DisplayMetrics dm = getBaseContext().getResources().getDisplayMetrics();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.locale = locale;

        getBaseContext().getResources().updateConfiguration(conf, dm);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DBManager db = new DBManager(MainActivity.this);
        ArrayMap settings = db.GetSettings();
        String lang = (String)settings.get("language");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.locale = locale;

        getBaseContext().getResources().updateConfiguration(conf, null);
    }

    private void makeTabs(ViewPager viewPager, final ArrayMap settings){
        this.tabs = findViewById(R.id.tabs);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = VERSION_CODES.M)
            @SuppressLint("ResourceType")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabLayout tabs = MainActivity.tabs;
                TypedValue typedValue;
                Window window;
                switch ((int)settings.get("theme")){
                    case (0):
                        switch (tabs.getSelectedTabPosition()){
                            case (0):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.tab1, typedValue, true);

                                tabs.setSelectedTabIndicatorColor(Color.WHITE);
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere_light);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task_light);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal_light);
                                tabs.setBackgroundColor(typedValue.data);

                                getTheme().resolveAttribute(R.attr.tab1Dark, typedValue, true);
                                window = getWindow();
                                window.setStatusBarColor(typedValue.data);
                                startTabGradient(currentTab, 0);
                                currentTab = 0;
                                break;
                            case (1):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.tab2, typedValue, true);

                                tabs.setSelectedTabIndicatorColor(Color.WHITE);
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere_light);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task_light);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal_light);
                                tabs.setBackgroundColor(typedValue.data);

                                getTheme().resolveAttribute(R.attr.tab2Dark, typedValue, true);
                                window = getWindow();
                                window.setStatusBarColor(typedValue.data);
                                startTabGradient(currentTab, 1);
                                currentTab = 1;
                                break;
                            case (2):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.tab3, typedValue, true);

                                PlaceholderFragment.sphereAnim = false;
                                tabs.setSelectedTabIndicatorColor(Color.WHITE);
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere_light);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task_light);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal_light);
                                tabs.setBackgroundColor(typedValue.data);

                                getTheme().resolveAttribute(R.attr.tab3Dark, typedValue, true);
                                window = getWindow();
                                window.setStatusBarColor(typedValue.data);
                                startTabGradient(currentTab, 2);
                                currentTab = 2;
                                break;
                        }
                        break;
                    case(1):
                        switch (tabs.getSelectedTabPosition()){
                            case (0):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);

                                tabs.setSelectedTabIndicatorColor(Color.parseColor("#CE93D8"));
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task_disable);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal_disable);
                                tabs.setBackgroundColor(typedValue.data);
                                break;
                            case (1):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);

                                tabs.setSelectedTabIndicatorColor(Color.parseColor("#EF9A9A"));
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere_disable);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal_disable);
                                tabs.setBackgroundColor(typedValue.data);
                                break;
                            case (2):
                                typedValue = new TypedValue();
                                getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);

                                PlaceholderFragment.sphereAnim = false;
                                tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFCC80"));
                                tabs.getTabAt(0).setIcon(R.drawable.ic_sphere_disable);
                                tabs.getTabAt(1).setIcon(R.drawable.ic_task_disable);
                                tabs.getTabAt(2).setIcon(R.drawable.ic_goal);
                                tabs.setBackgroundColor(typedValue.data);
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabs.setupWithViewPager(viewPager);
        tabs.selectTab(tabs.getTabAt(1));
    }

    private void startTabGradient(int lastTab, int curTab){
        final Window window = getWindow();

        TypedValue lastColor = new TypedValue();
        TypedValue lastColorDark = new TypedValue();
        switch (lastTab){
            case 0:
                getTheme().resolveAttribute(R.attr.tab1, lastColor, true);
                getTheme().resolveAttribute(R.attr.tab1Dark, lastColorDark, true);
                break;
            case 1:
                getTheme().resolveAttribute(R.attr.tab2, lastColor, true);
                getTheme().resolveAttribute(R.attr.tab2Dark, lastColorDark, true);
                break;
            case 2:
                getTheme().resolveAttribute(R.attr.tab3, lastColor, true);
                getTheme().resolveAttribute(R.attr.tab3Dark, lastColorDark, true);
                break;
        }
        TypedValue curColor = new TypedValue();
        TypedValue curColorDark = new TypedValue();
        switch (curTab){
            case 0:
                getTheme().resolveAttribute(R.attr.tab1, curColor, true);
                getTheme().resolveAttribute(R.attr.tab1Dark, curColorDark, true);
                break;
            case 1:
                getTheme().resolveAttribute(R.attr.tab2, curColor, true);
                getTheme().resolveAttribute(R.attr.tab2Dark, curColorDark, true);
                break;
            case 2:
                getTheme().resolveAttribute(R.attr.tab3, curColor, true);
                getTheme().resolveAttribute(R.attr.tab3Dark, curColorDark, true);
                break;
        }

        ValueAnimator colorAnim = ValueAnimator.ofArgb(lastColor.data, curColor.data)
                .setDuration(500);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tabs.setBackgroundColor((int)animation.getAnimatedValue());
            }
        });
        ValueAnimator colorAnimDark = ValueAnimator.ofArgb(lastColorDark.data, curColorDark.data)
                .setDuration(500);
        colorAnimDark.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                window.setStatusBarColor((int)animation.getAnimatedValue());
            }
        });
        colorAnim.start();
        colorAnimDark.start();
    }
}