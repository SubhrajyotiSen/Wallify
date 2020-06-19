package com.subhrajyoti.wallify.gallery

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.subhrajyoti.wallify.databinding.RecyclerItemBinding

internal class MainViewHolder(recyclerItemBinding: RecyclerItemBinding) : ViewHolder(recyclerItemBinding.root) {
    val thumbnail: ImageView = recyclerItemBinding.thumbnail
}