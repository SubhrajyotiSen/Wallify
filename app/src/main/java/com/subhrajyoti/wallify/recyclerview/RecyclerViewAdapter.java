package com.subhrajyoti.wallify.recyclerview;


import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhrajyoti.wallify.DownloadsGalleryActivity;
import com.subhrajyoti.wallify.R;
import com.subhrajyoti.wallify.db.ImageContract;

import java.io.File;

public class RecyclerViewAdapter extends CursorRecyclerAdapter<MainViewHolder> {

    public RecyclerViewAdapter(DownloadsGalleryActivity downloadsGalleryActivity, Cursor c) {
        super(downloadsGalleryActivity.getApplicationContext(), c);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, Cursor cursor, int position) {
        Log.d("PATH", cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_PATH)));
        viewHolder.thumbnail.setImageURI(Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_PATH)))));

    }
}