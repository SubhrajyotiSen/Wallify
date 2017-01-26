package com.subhrajyoti.wallify.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.subhrajyoti.wallify.R;

class MainViewHolder extends RecyclerView.ViewHolder {

    ImageView thumbnail;

    MainViewHolder(View itemView) {
        super(itemView);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

    }

}