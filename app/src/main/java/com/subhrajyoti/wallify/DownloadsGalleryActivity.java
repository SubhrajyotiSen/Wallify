package com.subhrajyoti.wallify;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.subhrajyoti.wallify.db.ImageContract;
import com.subhrajyoti.wallify.model.Image;
import com.subhrajyoti.wallify.recyclerview.RecyclerTouchListener;
import com.subhrajyoti.wallify.recyclerview.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadsGalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager linearLayoutManager;
    private ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        ButterKnife.bind(this);

        images = new ArrayList<>();

        recyclerViewAdapter = new RecyclerViewAdapter(images);
        linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        setSupportActionBar(toolbar);
        getSupportLoaderManager().initLoader(0, null,this );


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
                int index = images.get(position).getId();
                Log.d("TAG", String.valueOf(index));
                String selection = ImageContract.ImageEntry.IMAGE_ID + " = ?";
                getContentResolver().delete(ImageContract.ImageEntry.CONTENT_URI, selection, new String[]{String.valueOf(index)});
                (new File(images.get(position).getPath())).delete();

            }
        }));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(DownloadsGalleryActivity.this, ImageContract.ImageEntry.CONTENT_URI, null, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Count",String.valueOf(data.getCount()));
        while (data.moveToNext())
            images.add(new Image(
                            data.getInt(data.getColumnIndex(ImageContract.ImageEntry.IMAGE_ID)),
                            data.getString(data.getColumnIndex(ImageContract.ImageEntry.IMAGE_PATH))));
        recyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLoaderReset(Loader loader) {
    }
}
