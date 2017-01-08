package com.subhrajyoti.wallify;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;

public class SetWallpaperTask extends AsyncTask<Bitmap, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        boolean status= true;
        final Bitmap bitmap = params[0];
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(MyApplication.getContext());
        try {
            myWallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
