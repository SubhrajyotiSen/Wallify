package com.subhrajyoti.wallify.recyclerview;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhrajyoti.wallify.R;
import com.subhrajyoti.wallify.db.ImageContract;

public class RecyclerViewAdapter extends CursorRecyclerAdapter<MainViewHolder> {

    public RecyclerViewAdapter(Cursor c) {
        super(c);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new MainViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MainViewHolder holder, Cursor cursor) {
        holder.thumbnail.setImageBitmap(arrayToBitmap(cursor.getBlob(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_BLOB))));
    }

    private Bitmap arrayToBitmap(byte a[]){
        return BitmapFactory.decodeByteArray(a, 0, a.length);
    }

}