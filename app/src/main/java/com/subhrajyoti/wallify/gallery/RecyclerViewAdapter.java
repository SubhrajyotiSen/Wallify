package com.subhrajyoti.wallify.gallery;


import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhrajyoti.wallify.R;
import com.subhrajyoti.wallify.model.Image;

import java.io.File;
import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private ArrayList<Image> images;

    RecyclerViewAdapter(ArrayList<Image> arrayList) {
        images = arrayList;
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
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.thumbnail.setImageURI(Uri.fromFile(new File(images.get(position).getPath())));

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}