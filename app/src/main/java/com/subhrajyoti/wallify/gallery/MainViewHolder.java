package com.subhrajyoti.wallify.gallery;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.subhrajyoti.wallify.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class MainViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.thumbnail)
    ImageView thumbnail;

    MainViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}