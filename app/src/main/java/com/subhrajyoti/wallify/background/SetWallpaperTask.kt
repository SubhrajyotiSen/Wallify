package com.subhrajyoti.wallify.background

import android.graphics.Bitmap
import android.os.AsyncTask
import com.subhrajyoti.wallify.Utils.wallpaperManager
import java.io.IOException

class SetWallpaperTask : AsyncTask<Bitmap?, Void?, Boolean>() {
    override fun doInBackground(vararg params: Bitmap?): Boolean? {
        var status = true
        val bitmap = params[0]
        try {
            wallpaperManager.setBitmap(bitmap)
        } catch (e: IOException) {
            status = false
            e.printStackTrace()
        }
        return status
    }
}