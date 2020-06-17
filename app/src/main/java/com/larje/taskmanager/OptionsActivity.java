package com.larje.taskmanager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.optionDialogs.SettingsLanguageDialog;
import com.larje.taskmanager.ui.main.PlaceholderFragment;
import com.larje.taskmanager.ui.main.SectionsPagerAdapter;

import java.util.Locale;

public class OptionsActivity extends AppCompatActivity {

    private DBManager db;
    private ArrayMap settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.db = new DBManager(this);

        ArrayMap settings = db.GetSettings();
        switch((int)settings.get("theme")){
          case(0):
            setTheme(R.style.AppThemeLight);
            TypedValue typedValue = new TypedValue();
            Window window = getWindow();
            getTheme().resolveAttribute(R.attr.tab2Dark, typedValue, true);
            window.setStatusBarColor(typedValue.data);
            break;
          case(1):
            setTheme(R.style.AppThemeDark);
            break;
      }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


      makeBackButton();
      makeAds();
      settingsSystemTheme();
      settingsSystemLanguage();
      settingsTasks();
      settingsNotificationTime();
      settingsNotificationSwitch();
      settingsThemeButton();

    }

    private void makeBackButton(){
        ImageButton btn = findViewById(R.id.settings_back_button);
        switch(MainActivity.theme){
            case 0:
                btn.setImageDrawable(getDrawable(R.drawable.ic_back_light));
                break;
            case 1:
                btn.setImageDrawable(getDrawable(R.drawable.ic_back));
                break;

        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void makeAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        final TemplateView template = findViewById(R.id.settings_native_ad);

        TypedValue mainBackgrounColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, mainBackgrounColor, true);
        final TypedValue finalMainBackgrounColor = mainBackgrounColor;

        TypedValue buttonColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.tab2, buttonColor, true);
        final TypedValue finalbuttonColor = buttonColor;

        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-5090461340767825/5671057799")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        ColorDrawable mainBackground = new ColorDrawable();
                        mainBackground.setColor(finalMainBackgrounColor.data);
                        ColorDrawable actionBtnBackground = new ColorDrawable();
                        actionBtnBackground.setColor(finalbuttonColor.data);
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                .withMainBackgroundColor(mainBackground)
                                .withCallToActionBackgroundColor(actionBtnBackground)
                                .withPrimaryTextTypefaceColor(OptionsActivity.this.getColor(R.color.darkThemeLevel1subtext))
                                .withPrimaryTextBackgroundColor(mainBackground)
                                .withSecondaryTextTypefaceColor(OptionsActivity.this.getColor(R.color.darkThemeLevel1subtext))
                                .withSecondaryTextBackgroundColor(mainBackground)
                                .withTertiaryTextTypefaceColor(OptionsActivity.this.getColor(R.color.darkThemeLevel1subtext))
                                .withTertiaryTextBackgroundColor(mainBackground)
                                .build();
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
        if (adLoader.isLoading() && NetworkChecker.isNetworkEnable(this)){
            template.setVisibility(View.VISIBLE);
        }
    }

    private void settingsSystemTheme(){
        final Switch themeSwitch = findViewById(R.id.settings_theme);
        final ArrayMap settings = db.GetSettings();
        switch ((int)settings.get("theme")){
            case 0:
                themeSwitch.setChecked(false);
                break;
            case 1:
                themeSwitch.setChecked(true);
                break;
        }
        Log.d("settingsid", ""+(int)settings.get("id"));
        themeSwitch.setOnClickListener(new Switch.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeSwitch.isChecked()){
                    settings.put("theme", 1);
                    db.UpdateSettings(settings);
                } else {
                    settings.put("theme", 0);
                    db.UpdateSettings(settings);
                }
            }
        });

    }

    private void settingsSystemLanguage(){
        final LinearLayout langBtn = findViewById(R.id.settings_language_root);
        final TextView langTitle = findViewById(R.id.settings_language_title);
        settings = db.GetSettings();

        switch ((String)settings.get("language")){
            case ("ru"):
                langTitle.setText("Русский");
                break;
            case ("en"):
                langTitle.setText("English");
                break;
        }

        langBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                SettingsLanguageDialog dialog = new SettingsLanguageDialog(langTitle, OptionsActivity.this, getBaseContext());
                dialog.show(getSupportFragmentManager(), "s");
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void settingsTasks(){
        settings = db.GetSettings();
        final TextView title = findViewById(R.id.settings_task_txt);
        SeekBar seekBar = findViewById(R.id.settings_task_seekbar);

        final String[] titleText = ((String)title.getText()).split("xxx");
        title.setText(titleText[0] + settings.get("taskdays") + titleText[1]);

        seekBar.setProgress((int)settings.get("taskdays"));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.put("taskdays", progress);
                title.setText(titleText[0] + settings.get("taskdays") + titleText[1]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                settings = db.GetSettings();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                db.UpdateSettings(settings);
                MainActivity.view.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void updateLang(String lang){
        ArrayMap settings = db.GetSettings();
        settings.put("language", lang.toLowerCase());
        db.UpdateSettings(settings);
        System.exit(0);
    }

    public void settingsNotificationTime(){
        settings = db.GetSettings();
        final TextView title = findViewById(R.id.settings_notification_time_txt);
        SeekBar seekBar = findViewById(R.id.settings_notification_time_seekbar);

        final String[] titleText = ((String)title.getText()).split("xxx");
        title.setText(titleText[0] + settings.get("nottime") + titleText[1]);

        seekBar.setProgress((int)settings.get("nottime"));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.put("nottime", progress);
                title.setText(titleText[0] + settings.get("nottime") + titleText[1]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                settings = db.GetSettings();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                db.UpdateSettings(settings);
                MainActivity.view.getAdapter().notifyDataSetChanged();
                MainActivity.notificationAlarm.restart();
            }
        });
    }

    public void settingsNotificationSwitch(){
        final Switch notSwitch = findViewById(R.id.settings_notification_switch);
        ArrayMap settings = db.GetSettings();
        switch ((int)settings.get("notswitch")){
            case 0:
                notSwitch.setChecked(false);
                notSwitch.setText(getString(R.string.settings_set_notesswitch_off));
                break;
            case 1:
                notSwitch.setChecked(true);
                notSwitch.setText(getString(R.string.settings_set_notesswitch_on));
                break;
        }

        notSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayMap settings = db.GetSettings();
                if (isChecked){
                    notSwitch.setText(getString(R.string.settings_set_notesswitch_on));
                    settings.put("notswitch", 1);
                    db.UpdateSettings(settings);
                    MainActivity.notificationAlarm.restart();
                }else{
                    notSwitch.setText(getString(R.string.settings_set_notesswitch_off));
                    settings.put("notswitch", 0);
                    db.UpdateSettings(settings);
                    MainActivity.notificationAlarm.restart();
                }

                }
        });
    }

  private void settingsThemeButton(){
    final Switch themeSwitch = findViewById(R.id.settings_theme);
    settings = db.GetSettings();

    switch ((int)settings.get("theme")){
      case (0):
//        themeSwitch.setText(getString(R.string.settings_set_theme_light));
        themeSwitch.setChecked(false);
        break;
      case (1):
//        themeSwitch.setText(getString(R.string.settings_set_theme_dark));
        themeSwitch.setChecked(true);
        break;
    }

    themeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ArrayMap settings = db.GetSettings();
        if (isChecked){
          settings.put("theme", 1);
          db.UpdateSettings(settings);
//          themeSwitch.setText(getString(R.string.settings_set_theme_dark));
          System.exit(0);
        } else {
          settings.put("theme", 0);
          db.UpdateSettings(settings);
//          themeSwitch.setText(getString(R.string.settings_set_theme_light));
          System.exit(0);
        }
      }
    });

  }


//    public void makeBunner(final LinearLayout contentRoot){
//        final AdView bunner = new AdView(this);
//        bunner.setAdSize(AdSize.SMART_BANNER);
//        bunner.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
//
//
//        bunner.setAdListener(new AdListener(){
//            @Override
//            public void onAdLoaded(){
//                super.onAdLoaded();
//                Log.d("addsss", "ad is loaded");
//                bunner.setVisibility(View.VISIBLE);
//            }
//        });
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        bunner.loadAd(adRequest);
//        bunner.setVisibility(View.GONE);
//        contentRoot.addView(bunner);
//    }


}