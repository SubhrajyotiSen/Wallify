package com.subhrajyoti.wallify

import android.app.WallpaperManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.subhrajyoti.wallify.R.string
import java.io.File

object Utils {
    @JvmStatic
    val wallpaperManager: WallpaperManager
        get() = WallpaperManager.getInstance(MyApplication.context)

    @JvmStatic
    val backupImagePath: String
        get() = Environment.getExternalStorageDirectory()
                .toString() + File.separator + MyApplication.context.getString(string.app_name) + File.separator + "backup.png"

    @JvmStatic
    fun Toaster(string: Int) {
        Toast.makeText(MyApplication.context, MyApplication.context.getString(string), Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun isFirstRun(context: Context): Boolean {
        val PREFS_NAME = "MyPrefsFile"
        val PREF_VERSION_CODE_KEY = "version_code"
        val DOESNT_EXIST = -1

        // Get current version code
        val currentVersionCode = BuildConfig.VERSION_CODE

        // Get saved version code
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply()
        // Check for first run or upgrade
        return savedVersionCode == DOESNT_EXIST
    }
}