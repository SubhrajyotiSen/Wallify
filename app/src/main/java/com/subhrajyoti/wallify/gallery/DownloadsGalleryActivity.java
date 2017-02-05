package com.subhrajyoti.wallify.gallery;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.subhrajyoti.wallify.R;
import com.subhrajyoti.wallify.db.ImageContract;
import com.subhrajyoti.wallify.model.Image;

import org.polaric.colorful.CActivity;

import java.io.File;
import java.util.ArrayList;

public class DownloadsGalleryActivity extends CActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Image> images;

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.downloads));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        images = new ArrayList<>();

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        recyclerViewAdapter = new RecyclerViewAdapter(images);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

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
                if ((new File(images.get(position).getPath())).delete())
                    Toast.makeText(DownloadsGalleryActivity.this, R.string.image_deleted, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(DownloadsGalleryActivity.this, R.string.image_not_deleted, Toast.LENGTH_SHORT).show();
                images.remove(position);
                recyclerViewAdapter.notifyDataSetChanged();
                Log.d("SIZE", images.size() + "");
            }
        }));

        Bundle bundle = new Bundle();
        String ANALYTICS_ID = "Gallery";
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ID);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Gallery opened");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(DownloadsGalleryActivity.this, ImageContract.ImageEntry.CONTENT_URI, null, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Count",String.valueOf(data.getCount()));
        images.clear();
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
