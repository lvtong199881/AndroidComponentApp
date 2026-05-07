package com.mohanlv.shortvideo.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.ItemPhotoBinding
import com.mohanlv.shortvideo.model.Photo

/**
 * 照片列表适配器
 */
class PhotoAdapter(
    private val photos: List<Photo>,
    private val onItemClick: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.imagePhoto.setAspectRatio(photo.width, photo.height)
            binding.imagePhoto.setBackgroundColor(
                try { Color.parseColor(photo.avgColor ?: "#CCCCCC") }
                catch (e: Exception) { Color.parseColor("#CCCCCC") }
            )
            binding.imagePhoto.load(photo.src.large) {
                crossfade(true)
                placeholder(R.color.background)
                error(R.color.divider)
            }

            binding.root.setOnClickListener {
                onItemClick(photo)
            }
        }
    }
}