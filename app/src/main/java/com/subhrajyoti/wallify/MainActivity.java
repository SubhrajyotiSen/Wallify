package com.subhrajyoti.wallify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends CActivity implements NavigationView.OnNavigationItemSelectedListener {

    final private int REQUEST_STORAGE_PERM = 11;
    boolean grayscale;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.setFab)
    FloatingActionButton setFab;
    @BindView(R.id.saveFab)
    FloatingActionButton saveFab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private Animation fabClose, fabOpen, rotateBackward, rotateForward;
    private Bitmap bitmap;
    private Bitmap oldWallpaper;
    private SetWallpaperTask setWallpaperTask;
    private boolean isNew = false;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);

        if (Utils.isFirstRun(this))
            new TapTargetSequence(this)
                    .targets(
                            TapTarget
                                    .forView(findViewById(R.id.fab), "Click here to download or set Wallpaper")
                                    .outerCircleAlpha(0.7f)
                                    .transparentTarget(true)
                                    .cancelable(false),
                            TapTarget
                                    .forView(findViewById(R.id.imageView), "Click anywhere on the image to load a new Wallpaper")
                                    .outerCircleAlpha(0.7f)
                                    .transparentTarget(true)
                    )
                    .start();

        File file = new File(Utils.getBackupImagePath());
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            oldWallpaper = BitmapFactory.decodeFile(file.toString(), options);
        }

        if ((savedInstanceState == null))
            loadImage();

        imageView.setOnClickListener(view -> {
            loadImage();
            if (isOpen)
                animateFab();
        });

        fab.setOnClickListener(view -> animateFab());
        setFab.setOnClickListener(v -> {
            try {
                setWallpaper();
            } catch (ExecutionException | InterruptedException e) {
                Utils.Toaster(R.string.wallpaper_set_error);
                e.printStackTrace();
            }
            animateFab();
        });
        saveFab.setOnClickListener(view -> {
            try {
                saveImage();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            animateFab();
        });

    }

    public void loadImage() {
        isNew = true;
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

    public void saveImage() throws ExecutionException, InterruptedException {
        if (!isStorageGranted())
            requestPermission();
        else {
            generateCache();
            if (!isNew) {
                return;
            }
            boolean status = new SaveWallpaperTask().execute(new SaveWallpaperAsyncModel(bitmap, false)).get();
            if (status)
                Utils.Toaster(R.string.wallpaper_save_success);
            else
                Utils.Toaster(R.string.wallpaper_save_error);
            isNew = false;
        }
    }

    public void setWallpaper() throws ExecutionException, InterruptedException {
        generateCache();

        if (setWallpaperTask != null)
            setWallpaperTask.cancel(true);

        oldWallpaper = ((BitmapDrawable) Utils.getWallpaperManager().getDrawable()).getBitmap();

        setWallpaperTask = new SetWallpaperTask();
        boolean status = setWallpaperTask.execute(bitmap).get();
        if (status)
            Utils.Toaster(R.string.wallpaper_set_success);
        else
            Utils.Toaster(R.string.wallpaper_set_error);
        SaveWallpaperAsyncModel saveWallpaperAsyncModel = new SaveWallpaperAsyncModel(oldWallpaper, true);
        (new SaveWallpaperTask()).execute(saveWallpaperAsyncModel);

    }

    public void restoreWallpaper() throws ExecutionException, InterruptedException {
        if (oldWallpaper == null)
            Utils.Toaster(R.string.no_restore);
        else {
            if (new SetWallpaperTask().execute(oldWallpaper).get())
                Utils.Toaster(R.string.restore_success);
            else
                Utils.Toaster(R.string.restore_error);
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

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateBackward);
            saveFab.startAnimation(fabClose);
            setFab.startAnimation(fabClose);
            saveFab.setClickable(false);
            setFab.setClickable(false);
        } else {
            fab.startAnimation(rotateForward);
            saveFab.startAnimation(fabOpen);
            setFab.startAnimation(fabOpen);
            saveFab.setClickable(true);
            setFab.setClickable(true);
        }
        isOpen = !isOpen;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                saveImage();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name))
                .setMessage(R.string.no_permission)
                .setPositiveButton(getString(android.R.string.ok), null)
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
