package com.mohanlv.shortvideo.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.shortvideo.databinding.ItemVideoBinding
import com.mohanlv.shortvideo.model.Video

/**
 * 发现页面视频列表适配器（用于展示热门视频）
 */
class DiscoverVideoAdapter(
    private val videos: List<Video>,
    private val onItemClick: (Video) -> Unit
) : RecyclerView.Adapter<VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size
}