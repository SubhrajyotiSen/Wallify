package com.subhrajyoti.wallify

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy.NO_CACHE
import com.squareup.picasso.Picasso
import com.subhrajyoti.wallify.R.anim
import com.subhrajyoti.wallify.R.id
import com.subhrajyoti.wallify.R.layout
import com.subhrajyoti.wallify.R.string
import com.subhrajyoti.wallify.Utils.Toaster
import com.subhrajyoti.wallify.Utils.backupImagePath
import com.subhrajyoti.wallify.Utils.isFirstRun
import com.subhrajyoti.wallify.Utils.wallpaperManager
import com.subhrajyoti.wallify.background.SaveWallpaperTask
import com.subhrajyoti.wallify.background.SetWallpaperTask
import com.subhrajyoti.wallify.databinding.ActivityMainBinding
import com.subhrajyoti.wallify.gallery.DownloadsGalleryActivity
import com.subhrajyoti.wallify.model.SaveWallpaperAsyncModel
import org.polaric.colorful.CActivity
import java.io.File
import java.util.concurrent.ExecutionException

class MainActivity : CActivity(), OnNavigationItemSelectedListener {
    private val REQUEST_STORAGE_PERM = 11
    var grayscale = false
    private var fabClose: Animation? = null
    private var fabOpen: Animation? = null
    private var rotateBackward: Animation? = null
    private var rotateForward: Animation? = null
    private var bitmap: Bitmap? = null
    private var oldWallpaper: Bitmap? = null
    private var setWallpaperTask: SetWallpaperTask? = null
    private var isNew = false
    private var isOpen = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, string.open, string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        fabOpen = AnimationUtils.loadAnimation(this, anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, anim.fab_close)
        rotateBackward = AnimationUtils.loadAnimation(this, anim.rotate_backward)
        rotateForward = AnimationUtils.loadAnimation(this, anim.rotate_forward)
        if (isFirstRun(this)) TapTargetSequence(this)
                .targets(
                        TapTarget
                                .forView(findViewById(id.fab), "Click here to download or set Wallpaper")
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(true)
                                .cancelable(false),
                        TapTarget
                                .forView(findViewById(id.imageView), "Click anywhere on the image to load a new Wallpaper")
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(true)
                )
                .start()
        val file = File(backupImagePath)
        if (file.exists()) {
            val options = Options()
            options.inPreferredConfig = ARGB_8888
            oldWallpaper = BitmapFactory.decodeFile(file.toString(), options)
        }
        if (savedInstanceState == null) loadImage()
        binding.includedLayout.imageView.setOnClickListener { _ ->
            loadImage()
            if (isOpen) animateFab()
        }
        binding.fab.setOnClickListener {
            animateFab()
        }
        binding.setFab.setOnClickListener {
            try {
                setWallpaper()
            } catch (e: ExecutionException) {
                Toaster(string.wallpaper_set_error)
                e.printStackTrace()
            } catch (e: InterruptedException) {
                Toaster(string.wallpaper_set_error)
                e.printStackTrace()
            }
            animateFab()
        }
        binding.saveFab.setOnClickListener {
            try {
                saveImage()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            animateFab()
        }
    }

    fun loadImage() {
        isNew = true
        binding.includedLayout.progressBar.visibility = View.VISIBLE
        val string: String
        grayscale = PreferenceManager.getDefaultSharedPreferences(baseContext).getBoolean("grayscale", false)
        string = if (!grayscale) UrlConstants.NORMAL_URL else UrlConstants.GRAYSCALE_URL
        Picasso.with(this)
                .load(string)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NO_CACHE)
                .into(binding.includedLayout.imageView, object : Callback {
                    override fun onSuccess() {
                        binding.includedLayout.progressBar.visibility = View.GONE
                    }

                    override fun onError() {}
                })
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun saveImage() {
        if (!isStorageGranted) requestPermission() else {
            generateCache()
            if (!isNew) {
                return
            }
            val status = SaveWallpaperTask().execute(SaveWallpaperAsyncModel(bitmap!!, false)).get()
            if (status) Toaster(string.wallpaper_save_success) else Toaster(string.wallpaper_save_error)
            isNew = false
        }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun setWallpaper() {
        generateCache()
        if (setWallpaperTask != null) setWallpaperTask!!.cancel(true)
        oldWallpaper = (wallpaperManager.drawable as BitmapDrawable).bitmap
        setWallpaperTask = SetWallpaperTask()
        val status = setWallpaperTask!!.execute(bitmap).get()
        if (status) Toaster(string.wallpaper_set_success) else Toaster(string.wallpaper_set_error)
        val saveWallpaperAsyncModel = SaveWallpaperAsyncModel(oldWallpaper!!, true)
        SaveWallpaperTask().execute(saveWallpaperAsyncModel)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun restoreWallpaper() {
        if (oldWallpaper == null) Toaster(string.no_restore) else {
            if (SetWallpaperTask().execute(oldWallpaper).get()) Toaster(string.restore_success) else Toaster(string.restore_error)
            oldWallpaper = null
        }
    }

    private fun generateCache() {
        binding.includedLayout.imageView.destroyDrawingCache()
        binding.includedLayout.imageView.buildDrawingCache()
        bitmap = binding.includedLayout.imageView.getDrawingCache()
    }

    private val isStorageGranted: Boolean
        private get() = ActivityCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        val permissions = arrayOf(permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERM)
    }

    private fun animateFab() {
        if (isOpen) {
            binding.fab.startAnimation(rotateBackward)
            binding.saveFab.startAnimation(fabClose)
            binding.setFab.startAnimation(fabClose)
            binding.saveFab.setClickable(false)
            binding.setFab.setClickable(false)
        } else {
            binding.fab.startAnimation(rotateForward)
            binding.saveFab.startAnimation(fabOpen)
            binding.setFab.startAnimation(fabOpen)
            binding.saveFab.setClickable(true)
            binding.setFab.setClickable(true)
        }
        isOpen = !isOpen
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != REQUEST_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                saveImage()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return
        }
        val builder = Builder(this)
        builder.setTitle(getString(string.app_name))
                .setMessage(string.no_permission)
                .setPositiveButton(getString(android.R.string.ok), null)
                .show()
    }

    override fun onBackPressed() {
        System.exit(0)
        super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        loadImage()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val drawerListener: DrawerListener = object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                try {
                    restoreWallpaper()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            override fun onDrawerStateChanged(newState: Int) {}
        }
        when (id) {
            R.id.restore -> binding.drawerLayout.addDrawerListener(drawerListener)
            R.id.settings -> {
                binding.drawerLayout.removeDrawerListener(drawerListener)
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
            R.id.downloads -> {
                binding.drawerLayout.removeDrawerListener(drawerListener)
                startActivity(Intent(this@MainActivity, DownloadsGalleryActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}