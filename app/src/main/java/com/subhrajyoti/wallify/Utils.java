package com.subhrajyoti.wallify;

import android.app.WallpaperManager;
import android.os.Environment;

import java.io.File;

public class Utils {

    public static WallpaperManager getWallpaperManager() {
        return WallpaperManager.getInstance(MyApplication.getContext());
    }

    public static String getBackupImagePath() {
        return Environment.getExternalStorageDirectory()
                + File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator + "backup.png";
    }


}
