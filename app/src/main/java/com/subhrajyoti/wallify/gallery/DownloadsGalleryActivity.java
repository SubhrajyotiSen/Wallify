package com.subhrajyoti.wallify.gallery;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
}
