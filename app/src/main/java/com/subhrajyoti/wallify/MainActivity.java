package com.subhrajyoti.wallify;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobmead.easympermission.Permission;
import com.mobmead.easympermission.RuntimePermission;
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

@RuntimePermission
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.randomFab)
    FloatingActionButton randomFab;
    @Bind(R.id.setFab)
    FloatingActionButton setFab;
    @Bind(R.id.imageView)
    ImageView imageView;
    MaterialDialog materialDialog;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveImage();
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadImage() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content("Loading Image").progress(true, 0);

        materialDialog = builder.build();
        materialDialog.show();


        Picasso.with(this)
                .load("https://unsplash.it/1920/1080/?random")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Permission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void saveImage() {
        imageView.buildDrawingCache();
        bitmap = imageView.getDrawingCache();

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
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            assert fOut != null;
            fOut.flush();
            fOut.close();
            Toast.makeText(MainActivity.this, "Wallpaper Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    public void setWallpaper() {
        imageView.buildDrawingCache();
        bitmap = imageView.getDrawingCache();


        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(bitmap);
            Toast.makeText(MainActivity.this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

            e.printStackTrace();
        }


    }
}
