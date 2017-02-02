package com.subhrajyoti.wallify.background;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.subhrajyoti.wallify.R;

import java.util.concurrent.ExecutionException;

public class MyService extends Service {

    final private static String TAG = MyService.class.getSimpleName();
    private Target mTarget;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final boolean[] status = new boolean[1];
        String url;
        boolean grayscale = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("grayscale", false);
        if (grayscale)
            url = getString(R.string.grayscale_link);
        else
            url = getString(R.string.normal_link);
        Log.v(TAG, "service called");
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                Log.d(TAG, "Bitmap downloaded");
                try {
                    status[0] = new SetWallpaperTask().execute(bitmap).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (status[0])
                    Toast.makeText(MyService.this, R.string.wallpaper_set_success, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MyService.this, R.string.wallpaper_set_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.d(TAG, "Bitmap failed");
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d(TAG, "Bitmap onPrepareLoad");
            }
        };
        Picasso.with(this)
                .load(url)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mTarget);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

}

