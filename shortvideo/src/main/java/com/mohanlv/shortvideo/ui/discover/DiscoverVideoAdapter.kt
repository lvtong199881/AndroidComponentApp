package com.mohanlv.shortvideo.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.ItemVideoBinding
import com.mohanlv.shortvideo.model.Video

/**
 * 发现页面视频列表适配器（用于展示热门视频）
 */
class DiscoverVideoAdapter(
    private val videos: List<Video>,
    private val onItemClick: (Video) -> Unit
) : RecyclerView.Adapter<DiscoverVideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {

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
}