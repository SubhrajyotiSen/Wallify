package com.subhrajyoti.wallify.background

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.MemoryPolicy.NO_CACHE
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.subhrajyoti.wallify.R.string
import com.subhrajyoti.wallify.UrlConstants
import java.util.concurrent.ExecutionException

class MyService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MyService = this@MyService
    }


    private var mTarget: Target? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val status = BooleanArray(1)
        val url: String
        val grayscale = PreferenceManager.getDefaultSharedPreferences(baseContext).getBoolean("grayscale", false)
        url = if (grayscale) UrlConstants.GRAYSCALE_URL else UrlConstants.NORMAL_URL
        mTarget = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, loadedFrom: LoadedFrom) {
                try {
                    status[0] = SetWallpaperTask().execute(bitmap).get()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }
                if (status[0]) Toast.makeText(this@MyService, string.wallpaper_set_success, Toast.LENGTH_SHORT).show() else Toast.makeText(this@MyService, string.wallpaper_set_error, Toast.LENGTH_SHORT).show()
            }

            override fun onBitmapFailed(drawable: Drawable) {
                Log.d(TAG, "Bitmap failed")
            }

            override fun onPrepareLoad(drawable: Drawable) {
                Log.d(TAG, "Bitmap onPrepareLoad")
            }
        }
        Picasso.with(this)
                .load(url)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(NO_CACHE)
                .into(mTarget)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    companion object {
        private val TAG = MyService::class.java.simpleName
    }
}