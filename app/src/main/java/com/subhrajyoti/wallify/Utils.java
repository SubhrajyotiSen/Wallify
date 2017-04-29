package com.subhrajyoti.wallify;

import android.app.WallpaperManager;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

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
}
