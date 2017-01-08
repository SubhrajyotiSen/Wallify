package com.subhrajyoti.wallify;


import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import org.polaric.colorful.Colorful;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        MyApplication.context = getApplicationContext();

        boolean dark = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("dark", false);
        Colorful.config(getApplicationContext())
                .primaryColor(Colorful.ThemeColor.BLUE)
                .accentColor(Colorful.ThemeColor.PINK)
                .translucent(false)
                .dark(dark)
                .apply();

        Colorful.init(this);
    }

    public static Context getContext(){
        return MyApplication.context;
    }
}