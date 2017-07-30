package com.subhrajyoti.wallify;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static WallpaperManager getWallpaperManager() {
        return WallpaperManager.getInstance(MyApplication.getContext());
    }

    public static String getBackupImagePath() {
        return Environment.getExternalStorageDirectory()
                + File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator + "backup.png";
    }

    public static void Toaster(int string) {
        Toast.makeText(MyApplication.getContext(), MyApplication.getContext().getString(string), Toast.LENGTH_SHORT).show();
    }

    public static boolean isFirstRun(Context context) {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        // Check for first run or upgrade
        return savedVersionCode == DOESNT_EXIST;
    }
}
