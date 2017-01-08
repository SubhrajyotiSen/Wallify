package com.subhrajyoti.wallify;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
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
            string = "https://unsplash.it/1920/1080/?random";
        else
            string = "https://unsplash.it/g/1920/1080/?random";
        Picasso.with(this)
                .load(string)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //materialDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public void saveImage() {
        getPermissions();
        generateCache();
        SaveWallpaperTask saveTask = new SaveWallpaperTask();
        saveTask.execute(bitmap);

    }

    public void setWallpaper() {

        generateCache();
        SetWallpaperTask setWallpaper = new SetWallpaperTask();
        setWallpaper.execute(bitmap);
    }

    public class SetWallpaperTask extends AsyncTask<Bitmap, Void,
            Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            final Bitmap bitmap = params[0];
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getApplicationContext());
            try {
                myWallpaperManager.setBitmap(bitmap);
                Toast.makeText(getApplicationContext(), "Wallpaper Set", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }

    }

    private void getPermissions(){

        int storagePermission;
        String permission = "TAG";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            storagePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if( storagePermission != PackageManager.PERMISSION_GRANTED ) {
                permission =  Manifest.permission.WRITE_EXTERNAL_STORAGE ;
            }
            if( !permission.equals("TAG")) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        12);
            }
        }
    }

    public class SaveWallpaperTask extends AsyncTask<Bitmap, Void,
            Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            final Bitmap bitmap = params[0];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();

            OutputStream fOut = null;
            try {
                File root = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "Wallify" + File.separator);
                root.mkdirs();
                File sdImageMainDirectory = new File(root, formatter.format(now) + ".jpg");
                fOut = new FileOutputStream(sdImageMainDirectory);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error occured. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }

            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                assert fOut != null;
                fOut.flush();
                fOut.close();
                Toast.makeText(MainActivity.this, "Wallpaper Saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private void generateCache(){
        imageView.destroyDrawingCache();
        imageView.buildDrawingCache();
        bitmap = imageView.getDrawingCache();
    }

}
