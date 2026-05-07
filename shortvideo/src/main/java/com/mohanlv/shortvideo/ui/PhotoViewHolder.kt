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
 * 照片列表 ViewHolder
 */
class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photo: Photo, payloads: MutableList<Any> = mutableListOf()) {
        if (payloads.isEmpty()) {
            binding.imagePhoto.setAspectRatio(photo.width, photo.height)
            binding.imagePhoto.setBackgroundColor(
                try { Color.parseColor(photo.avgColor ?: "#CCCCCC") }
                catch (_: Exception) { Color.parseColor("#CCCCCC") }
            )
            binding.imagePhoto.load(photo.src.large) {
                crossfade(true)
                placeholder(R.color.background)
                error(R.color.divider)
            }
        }

        binding.root.setOnClickListener {
            // onItemClick(photo)
        }
    }

    companion object {
        fun from(parent: ViewGroup): PhotoViewHolder {
            val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PhotoViewHolder(binding)
        }
    }
}