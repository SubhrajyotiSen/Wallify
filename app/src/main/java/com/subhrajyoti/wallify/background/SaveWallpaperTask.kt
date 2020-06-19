package com.subhrajyoti.wallify.background

import android.graphics.Bitmap.CompressFormat.PNG
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.subhrajyoti.wallify.MyApplication.Companion.context
import com.subhrajyoti.wallify.R.string
import com.subhrajyoti.wallify.Utils.backupImagePath
import com.subhrajyoti.wallify.model.SaveWallpaperAsyncModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveWallpaperTask : AsyncTask<SaveWallpaperAsyncModel?, Void?, Boolean>() {
    override fun doInBackground(vararg params: SaveWallpaperAsyncModel?): Boolean? {
        val bitmap = params[0]!!.bitmap
        val CODE = params[0]!!.bACKUP
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH)
        val now = Date()
        var status = true
        val context = context
        val fOut: OutputStream
        try {
            val root = File(Environment.getExternalStorageDirectory()
                    .toString() + File.separator + context.getString(string.app_name) + File.separator)
            if (root.mkdirs() || root.exists()) {
                val filename: String
                if (CODE) {
                    filename = "backup"
                    val file = File(backupImagePath)
                    val TAG = "SaveWallpaperTask"
                    if (file.exists()) if (file.delete()) Log.d(TAG, "Deleted") else Log.d(TAG, "Deletion failed")
                } else filename = formatter.format(now)
                val sdImageMainDirectory = File(root, "$filename.png")
                fOut = FileOutputStream(sdImageMainDirectory)
                bitmap.compress(PNG, 100, fOut)
                fOut.flush()
                fOut.close()
            }
        } catch (e: Exception) {
            status = false
            e.printStackTrace()
        }
        return status
    }
}