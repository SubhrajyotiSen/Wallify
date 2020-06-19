package com.subhrajyoti.wallify.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.subhrajyoti.wallify.databinding.RecyclerItemBinding
import java.io.File
import java.util.ArrayList

internal class RecyclerViewAdapter(private val images: ArrayList<String>) : Adapter<MainViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MainViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.thumbnail.setImageURI(Uri.fromFile(File(images[position])))
    }

    override fun getItemCount() = images.size

}