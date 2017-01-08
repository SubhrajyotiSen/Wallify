package com.subhrajyoti.wallify;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.polaric.colorful.CActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends CActivity {

    @Bind(R.id.randomFab)
    FloatingActionButton randomFab;
    @Bind(R.id.setFab)
    FloatingActionButton setFab;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    Bitmap bitmap;
    boolean grayscale;
    final private int REQUEST_STORAGE_PERM = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        if (!isStorageGranted())
            requestPermission();
        if ((savedInstanceState==null))
        loadImage();
        randomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
        setFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                saveImage();
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    public void loadImage() {

        progressBar.setVisibility(View.VISIBLE);
        String string;
        grayscale = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("grayscale", false);
        if (!grayscale)
            string = getString(R.string.normal_link);
        else
            string = getString(R.string.grayscale_link);
        Picasso.with(this)
                .load(string)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public void saveImage() {
        generateCache();
        new SaveWallpaperTask().execute(bitmap);
    }

    public void setWallpaper() {
        generateCache();
        new SetWallpaperTask().execute(bitmap);
    }

    public class SetWallpaperTask extends AsyncTask<Bitmap, Void,Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            boolean status = true;
            final Bitmap bitmap = params[0];
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getApplicationContext());
            try {
                myWallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                status = false;
                e.printStackTrace();
            }
            finally {
                final boolean finalStatus = status;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalStatus)
                            Toast.makeText(getApplicationContext(), R.string.wallpaper_set_success, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), R.string.wallpaper_set_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }



    public class SaveWallpaperTask extends AsyncTask<Bitmap, Void,
            Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            final Bitmap bitmap = params[0];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
            Date now = new Date();
            boolean status = true;
            OutputStream fOut ;
            try {
                File root = new File(Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.app_name) + File.separator);
                if(root.mkdirs()) {
                    File sdImageMainDirectory = new File(root, formatter.format(now) + ".jpg");
                    fOut = new FileOutputStream(sdImageMainDirectory);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                }
            } catch (Exception e) {
                status = false;
                e.printStackTrace();
            } finally {
                final boolean finalStatus = status;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalStatus)
                            Toast.makeText(MainActivity.this, R.string.wallpaper_save_success, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, R.string.wallpaper_save_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

    }

    private void generateCache(){
        imageView.destroyDrawingCache();
        imageView.buildDrawingCache();
        bitmap = imageView.getDrawingCache();
    }

    private boolean isStorageGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name))
                .setMessage(R.string.no_permission)
                .setPositiveButton(getString(android.R.string.ok), listener)
                .show();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        super.onBackPressed();
    }

}
