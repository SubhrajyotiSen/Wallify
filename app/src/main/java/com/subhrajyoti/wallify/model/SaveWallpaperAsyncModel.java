package com.subhrajyoti.wallify.model;


import android.graphics.Bitmap;

public class SaveWallpaperAsyncModel {

    private Bitmap bitmap;
    private boolean BACKUP;
    /* false is for saving new wallpaper
       true is for saving backup wallpaper
     */

    public SaveWallpaperAsyncModel(Bitmap bitmap, boolean BACKUP) {
        this.bitmap = bitmap;
        this.BACKUP = BACKUP;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean getBACKUP() {
        return BACKUP;
    }
}
