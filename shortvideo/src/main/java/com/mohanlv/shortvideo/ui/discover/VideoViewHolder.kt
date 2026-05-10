package com.mohanlv.shortvideo.ui.discover

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.ItemVideoBinding
import com.mohanlv.shortvideo.model.Video

/**
 * 视频 ViewHolder
 */
class VideoViewHolder(
    private val binding: ItemVideoBinding,
    private val onItemClick: (Video) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(video: Video) {
        binding.imageThumbnail.setAspectRatio(video.width, video.height)
        binding.imageThumbnail.load(video.image) {
            crossfade(true)
            placeholder(R.color.background)
            error(R.color.divider)
        }
        binding.textDuration.text = video.getFormattedDuration()

        binding.root.setOnClickListener {
            onItemClick(video)
        }
    }
}