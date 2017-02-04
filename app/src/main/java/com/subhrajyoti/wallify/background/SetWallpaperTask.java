package com.subhrajyoti.wallify.background;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.subhrajyoti.wallify.Utils;

import java.io.IOException;

public class SetWallpaperTask extends AsyncTask<Bitmap, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        boolean status= true;
        final Bitmap bitmap = params[0];
        try {
            Utils.getWallpaperManager().setBitmap(bitmap);
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}