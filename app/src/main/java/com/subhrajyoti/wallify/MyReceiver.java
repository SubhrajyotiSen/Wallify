package com.subhrajyoti.wallify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("hahaha","rev");
        Intent intent1 = new Intent(context,MyService.class);
        context.startService(intent1);
    }
}
