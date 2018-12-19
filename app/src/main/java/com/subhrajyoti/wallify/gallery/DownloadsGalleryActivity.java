package com.subhrajyoti.wallify.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.subhrajyoti.wallify.R;

import org.polaric.colorful.CActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsGalleryActivity extends CActivity {

    final private int REQUEST_STORAGE_PERM = 11;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> images;

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        ButterKnife.bind(this);

        assert toolbar != null;
        toolbar.setTitle(getString(R.string.downloads));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        images = new ArrayList<>();

        recyclerViewAdapter = new RecyclerViewAdapter(images);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (!isStorageGranted())
            requestPermission();
        else
            loadImages();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                FullscreenDialog newFragment = FullscreenDialog.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }

            @Override
            public void onLongClick(View view, int position) {
                File file = new File(images.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadsGalleryActivity.this);
                builder.setTitle(R.string.delete_image);
                builder.setMessage(R.string.delete_image_warning);


                builder.setNegativeButton(R.string.no,
                        (dialog, which) -> {
                        });

                builder.setPositiveButton(R.string.yes,
                        (dialog, which) -> {
                            if (file.delete()) {
                                images.remove(position);
                                recyclerViewAdapter.notifyDataSetChanged();
                                Toast.makeText(DownloadsGalleryActivity.this, R.string.image_delete_confirm, Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(DownloadsGalleryActivity.this, R.string.image_delete_error, Toast.LENGTH_SHORT).show();

                        });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }
        }));


    }

    private void loadImages() {
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + getString(R.string.app_name) + File.separator);
        if ( root.isDirectory()){
            for (File file : root.listFiles())
                images.add(file.getAbsoluteFile().toString());
        }
        recyclerViewAdapter.notifyDataSetChanged();
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
            loadImages();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name))
                .setMessage(R.string.no_permission)
                .setPositiveButton(getString(android.R.string.ok), (dialogInterface, i) -> requestPermission())
                .show();
    }

}
