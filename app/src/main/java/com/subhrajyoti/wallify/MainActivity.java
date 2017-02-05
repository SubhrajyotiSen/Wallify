package com.subhrajyoti.wallify;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.subhrajyoti.wallify.background.SaveWallpaperTask;
import com.subhrajyoti.wallify.background.SetWallpaperTask;
import com.subhrajyoti.wallify.gallery.DownloadsGalleryActivity;
import com.subhrajyoti.wallify.model.SaveWallpaperAsyncModel;

import org.polaric.colorful.CActivity;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MainActivity extends CActivity implements NavigationView.OnNavigationItemSelectedListener {

    final private int REQUEST_STORAGE_PERM = 11;
    private final String ANALYTICS_ID = "Main";
    boolean grayscale;
    private ImageView imageView;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private Bitmap bitmap;
    private Bitmap oldWallpaper;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton randomFab = (FloatingActionButton) findViewById(R.id.randomFab);
        FloatingActionButton setFab = (FloatingActionButton) findViewById(R.id.setFab);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (!isStorageGranted())
            requestPermission();
        File file = new File(Utils.getBackupImagePath());
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            oldWallpaper = BitmapFactory.decodeFile(file.toString(), options);
        }

        if ((savedInstanceState == null))
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
                try {
                    setWallpaper();
                } catch (ExecutionException | InterruptedException e) {
                    Toast.makeText(MainActivity.this, R.string.wallpaper_set_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
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
                try {
                    saveImage();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;

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

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "New Image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void saveImage() throws ExecutionException, InterruptedException {
        generateCache();
        boolean status = new SaveWallpaperTask().execute(new SaveWallpaperAsyncModel(bitmap, false)).get();
        if (status)
            Toast.makeText(MainActivity.this, R.string.wallpaper_save_success, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, R.string.wallpaper_save_error, Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Save image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void setWallpaper() throws ExecutionException, InterruptedException {
        generateCache();

        oldWallpaper = ((BitmapDrawable) Utils.getWallpaperManager().getDrawable()).getBitmap();

        boolean status = new SetWallpaperTask().execute(bitmap).get();
        if (status)
            Toast.makeText(this, R.string.wallpaper_set_success, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.wallpaper_set_error, Toast.LENGTH_SHORT).show();
        SaveWallpaperAsyncModel saveWallpaperAsyncModel = new SaveWallpaperAsyncModel(oldWallpaper, true);
        (new SaveWallpaperTask()).execute(saveWallpaperAsyncModel);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void restoreWallpaper() throws ExecutionException, InterruptedException {
        if (oldWallpaper == null)
            Toast.makeText(this, R.string.no_restore, Toast.LENGTH_SHORT).show();
        else {
            if (new SetWallpaperTask().execute(oldWallpaper).get())
                Toast.makeText(this, R.string.restore_success, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.restore_error, Toast.LENGTH_SHORT).show();
            oldWallpaper = null;
        }
    }

    private void generateCache() {
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        loadImage();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                try {
                    restoreWallpaper();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        switch (id) {
            case R.id.restore:
                drawerLayout.addDrawerListener(drawerListener);
                break;
            case R.id.settings:
                drawerLayout.removeDrawerListener(drawerListener);
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.downloads:
                drawerLayout.removeDrawerListener(drawerListener);
                startActivity(new Intent(MainActivity.this, DownloadsGalleryActivity.class));
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

}
