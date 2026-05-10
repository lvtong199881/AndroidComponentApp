package com.mohanlv.shortvideo.ui.photos

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.shortvideo.model.Photo

/**
 * 照片列表适配器
 */
class PhotoAdapter(
    private val photos: List<Photo>,
    private val onItemClick: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(photos[position], payloads)
    }
}