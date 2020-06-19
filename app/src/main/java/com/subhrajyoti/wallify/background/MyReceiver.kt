package com.subhrajyoti.wallify.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import com.subhrajyoti.wallify.MyApplication

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (PreferenceManager.getDefaultSharedPreferences(MyApplication.context).getBoolean("daily", false)) {
            val intent1 = Intent(context, MyService::class.java)
            context.startService(intent1)
        }
    }
}