package com.mohanlv.home.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mohanlv.home.R
import com.mohanlv.home.databinding.ItemCollectionMediaBinding
import com.mohanlv.home.model.MediaItem

/**
 * 合集媒体 ViewHolder
 */
class CollectionMediaViewHolder(
    private val binding: ItemCollectionMediaBinding,
    private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var currentMedia: MediaItem? = null

    init {
        binding.root.setOnClickListener {
            currentMedia?.let { onItemClick(it) }
        }
    }

    fun bind(media: MediaItem) {
        currentMedia = media

        // 设置宽高比
        val width = media.width ?: 1
        val height = media.height ?: 1
        binding.ivThumbnail.setAspectRatio(width, height)

        // 加载图片
        val imageUrl = when (media.type) {
            "photo" -> media.src?.medium ?: media.src?.large
            "video" -> media.image
            "Video" -> media.image
            else -> media.src?.medium ?: media.image
        }
        binding.ivThumbnail.load(imageUrl) {
            crossfade(true)
            placeholder(android.R.color.darker_gray)
        }

        // 视频显示播放图标和时长
        if (media.type == "video" || media.type == "Video") {
            binding.ivPlayIcon.visibility = View.VISIBLE
            if (media.duration != null && media.duration > 0) {
                binding.tvDuration.visibility = View.VISIBLE
                binding.tvDuration.text = formatDuration(media.duration)
            } else {
                binding.tvDuration.visibility = View.GONE
            }
        } else {
            binding.ivPlayIcon.visibility = View.GONE
            binding.tvDuration.visibility = View.GONE
        }
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }
}