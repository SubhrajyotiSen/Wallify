package com.subhrajyoti.wallify

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import org.polaric.colorful.Colorful
import org.polaric.colorful.Colorful.ThemeColor.BLUE
import org.polaric.colorful.Colorful.ThemeColor.PINK

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        val dark = PreferenceManager.getDefaultSharedPreferences(baseContext).getBoolean("dark", false)
        Colorful.config(applicationContext)
                .primaryColor(BLUE)
                .accentColor(PINK)
                .translucent(false)
                .dark(dark)
                .apply()
        Colorful.init(this)
    }

    companion object {
        @JvmStatic
        lateinit var context: Context
            private set
    }
}