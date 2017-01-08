package com.subhrajyoti.wallify;


import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import org.polaric.colorful.Colorful;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean dark = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("dark", false);
        Colorful.config(getApplicationContext())
                .primaryColor(Colorful.ThemeColor.BLUE)
                .accentColor(Colorful.ThemeColor.PINK)
                .translucent(false)
                .dark(dark)
                .apply();
        Log.d("TAG",dark+"");
        Colorful.init(this);
    }
}