package com.subhrajyoti.wallify.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.subhrajyoti.wallify.MyApplication;

public class MyReceiver extends BroadcastReceiver {

    final private static String TAG = MyReceiver.class.getSimpleName();

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if( PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("daily", false)) {
            Intent intent1 = new Intent(context, MyService.class);
            context.startService(intent1);
        }
    }
}
