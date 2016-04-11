package com.subhrajyoti.wallify;

import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATEActivity;

public class BaseThemeActivity extends ATEActivity {

    @Nullable
    @Override
    public final String getATEKey() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }
}
