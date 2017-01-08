package com.subhrajyoti.wallify;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    final private static String TAG = "Wallify";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG,"date");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

}

