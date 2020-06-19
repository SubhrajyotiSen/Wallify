package com.subhrajyoti.wallify.gallery

import android.Manifest.permission
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.subhrajyoti.wallify.R
import com.subhrajyoti.wallify.databinding.ActivityFavBinding
import com.subhrajyoti.wallify.gallery.RecyclerTouchListener.ClickListener
import org.polaric.colorful.CActivity
import java.io.File
import java.util.ArrayList

class DownloadsGalleryActivity : CActivity() {

    private val REQUEST_STORAGE_PERM = 11

    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var images: ArrayList<String>? = null

    private lateinit var binding: ActivityFavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.downloads)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        images = ArrayList()
        recyclerViewAdapter = RecyclerViewAdapter(images!!)
        val linearLayoutManager = GridLayoutManager(this, calculateNoOfColumns(this))
        binding.includedLayout.recyclerView.layoutManager = linearLayoutManager
        binding.includedLayout.recyclerView.adapter = recyclerViewAdapter
        if (!isStorageGranted) requestPermission() else loadImages()
        binding.includedLayout.recyclerView.addOnItemTouchListener(RecyclerTouchListener(this, binding.includedLayout.recyclerView, object : ClickListener {
            override fun onClick(view: View?, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("images", images)
                bundle.putInt("position", position)
                val ft = supportFragmentManager.beginTransaction()
                val newFragment = FullscreenDialog.newInstance()
                newFragment.arguments = bundle
                newFragment.show(ft, "slideshow")
            }

            override fun onLongClick(view: View?, position: Int) {
                val file = File(images!![position])
                val builder = Builder(this@DownloadsGalleryActivity)
                builder.setTitle(R.string.delete_image)
                builder.setMessage(R.string.delete_image_warning)
                builder.setNegativeButton(R.string.no
                ) { dialog: DialogInterface?, which: Int -> }
                builder.setPositiveButton(R.string.yes
                ) { dialog: DialogInterface?, which: Int ->
                    if (file.delete()) {
                        images!!.removeAt(position)
                        recyclerViewAdapter!!.notifyDataSetChanged()
                        Toast.makeText(this@DownloadsGalleryActivity, R.string.image_delete_confirm, Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this@DownloadsGalleryActivity, R.string.image_delete_error, Toast.LENGTH_SHORT).show()
                }
                val dialog = builder.create()
                // display dialog
                dialog.show()
            }
        }))
    }

    private fun loadImages() {
        val root = File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + getString(R.string.app_name) + File.separator)
        if (root.isDirectory) {
            for (file in root.listFiles()) images!!.add(file.absoluteFile.toString())
        }
        recyclerViewAdapter!!.notifyDataSetChanged()
    }

    private val isStorageGranted: Boolean
        private get() = ActivityCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        val permissions = arrayOf(permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != REQUEST_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImages()
            return
        }
        val builder = Builder(this)
        builder.setTitle(getString(R.string.app_name))
                .setMessage(R.string.no_permission)
                .setPositiveButton(getString(R.string.ok)) { _, _ -> requestPermission() }
                .show()
    }

    companion object {
        fun calculateNoOfColumns(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            return (dpWidth / 180).toInt()
        }
    }
}