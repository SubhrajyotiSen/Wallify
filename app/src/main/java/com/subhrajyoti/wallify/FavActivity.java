package com.subhrajyoti.wallify;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.subhrajyoti.wallify.db.ImageContract;
import com.subhrajyoti.wallify.recyclerview.RecyclerViewAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        ButterKnife.bind(this);

        recyclerViewAdapter = new RecyclerViewAdapter(null);
        linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        setSupportActionBar(toolbar);
        getSupportLoaderManager().initLoader(0, null,this );

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ImageContract.ImageEntry.IMAGE_ID,
                ImageContract.ImageEntry.IMAGE_BLOB};
        return new CursorLoader(FavActivity.this, ImageContract.ImageEntry.CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Count",String.valueOf(data.getCount()));
        recyclerViewAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        recyclerViewAdapter.swapCursor(null);
    }
}
