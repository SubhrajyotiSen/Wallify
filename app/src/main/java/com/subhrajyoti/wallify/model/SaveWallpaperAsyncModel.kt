package com.subhrajyoti.wallify.model

import android.graphics.Bitmap

class SaveWallpaperAsyncModel /* false is for saving new wallpaper
       true is for saving backup wallpaper
     */(val bitmap: Bitmap, val bACKUP: Boolean)