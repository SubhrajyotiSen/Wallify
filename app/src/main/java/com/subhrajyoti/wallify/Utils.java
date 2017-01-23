package com.subhrajyoti.wallify;

import android.app.WallpaperManager;

public class Utils {

    public static WallpaperManager getWallpaperManager(){
        return WallpaperManager.getInstance(MyApplication.getContext());
    }
}
