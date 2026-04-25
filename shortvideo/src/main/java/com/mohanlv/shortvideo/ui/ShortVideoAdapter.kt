package com.mohanlv.shortvideo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.shortvideo.databinding.ItemShortVideoBinding
import com.mohanlv.shortvideo.model.Video
import java.util.Timer

/**
 * 短视频适配器
 * 使用 ViewPager2 竖向滑动切换视频
 */
class ShortVideoAdapter(
    private val videos: List<Video>,
    private val onLikeClick: (Video) -> Unit,
    private val onCommentClick: (Video) -> Unit,
    private val onShareClick: (Video) -> Unit
) : RecyclerView.Adapter<VideoViewHolder>() {

    private var currentPlayingHolder: VideoViewHolder? = null
    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemShortVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onLikeClick,
            onCommentClick,
            onShareClick
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        if (position == currentPosition) {
            currentPlayingHolder?.pauseVideo()
            currentPlayingHolder = holder
            holder.playVideo()
        }
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.pauseVideo()
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.release()
    }

    fun setCurrentPosition(position: Int) {
        val oldPosition = currentPosition
        currentPosition = position
        if (oldPosition != position) {
            notifyItemChanged(oldPosition)
            notifyItemChanged(position)
        }
    }

    fun releaseAll() {
        currentPlayingHolder?.pauseVideo()
        progressTimer?.cancel()
    }

    fun release() {
        progressTimer?.cancel()
    }

    companion object {
        private var progressTimer: Timer? = null

        fun cancelGlobalTimer() {
            progressTimer?.cancel()
            progressTimer = null
        }
    }
}