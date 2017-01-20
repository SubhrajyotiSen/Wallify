package com.subhrajyoti.wallify;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveWallpaperTask extends AsyncTask<Bitmap, Void,
        Boolean> {

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        final Bitmap bitmap = params[0];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        Date now = new Date();
        boolean status = true;
        Context context = MyApplication.getContext();
        OutputStream fOut ;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + context.getString(R.string.app_name) + File.separator);
            if(root.mkdirs()) {
                File sdImageMainDirectory = new File(root, formatter.format(now) + ".jpg");
                fOut = new FileOutputStream(sdImageMainDirectory);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

}