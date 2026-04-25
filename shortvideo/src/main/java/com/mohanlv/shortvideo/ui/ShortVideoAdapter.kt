package com.mohanlv.shortvideo.ui

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.base.utils.ImageLoader
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.ItemShortVideoBinding
import com.mohanlv.shortvideo.model.Video
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt

/**
 * 短视频适配器
 * 使用 ViewPager2 竖向滑动切换视频
 */
class ShortVideoAdapter(
    private val videos: List<Video>,
    private val onLikeClick: (Video) -> Unit,
    private val onCommentClick: (Video) -> Unit,
    private val onShareClick: (Video) -> Unit
) : RecyclerView.Adapter<ShortVideoAdapter.VideoViewHolder>() {

    private var currentPlayingHolder: VideoViewHolder? = null
    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemShortVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    }

    inner class VideoViewHolder(
        private val binding: ItemShortVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var video: Video? = null
        private var isPlaying = false
        private var progressTimer: Timer? = null

        private val progressUpdateTask = object : TimerTask() {
            override fun run() {
                if (isPlaying && binding.videoView.isPlaying) {
                    binding.root.post {
                        updateProgress()
                    }
                }
            }
        }

        fun bind(video: Video) {
            this.video = video

            // 加载封面图
            ImageLoader.load(binding.ivCover, video.image)
            binding.ivCover.visibility = View.VISIBLE
            binding.ivPlayIcon.visibility = View.VISIBLE

            // 设置作者和标题
            binding.tvAuthor.text = "@${video.user.name}"
            binding.tvTitle.text = video.url

            // 设置时长
            binding.tvDuration.text = video.getFormattedDuration()
            binding.tvCurrentTime.text = "00:00"
            binding.seekBarProgress.progress = 0

            // 设置点赞、评论、分享数（模拟数据）
            binding.tvLikeCount.text = formatCount((1000..50000).random())
            binding.tvCommentCount.text = formatCount((100..10000).random())
            binding.tvShareCount.text = itemView.context.getString(R.string.share)

            // 播放按钮点击
            binding.ivPlayIcon.setOnClickListener {
                playVideo()
            }

            // 封面点击
            binding.ivCover.setOnClickListener {
                playVideo()
            }

            // 视频点击暂停/播放
            binding.videoView.setOnClickListener {
                if (isPlaying) {
                    pauseVideo()
                } else {
                    playVideo()
                }
            }

            // 点赞点击
            binding.ivLike.setOnClickListener {
                video?.let { onLikeClick(it) }
            }

            // 评论点击
            binding.root.findViewById<View>(R.id.tvCommentCount)?.setOnClickListener {
                video?.let { onCommentClick(it) }
            }

            // 分享点击
            binding.root.findViewById<View>(R.id.tvShareCount)?.setOnClickListener {
                video?.let { onShareClick(it) }
            }

            // SeekBar 拖动
            binding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val duration = binding.videoView.duration
                        val newPosition = (duration * progress / 100f).roundToInt()
                        binding.videoView.seekTo(newPosition)
                        binding.tvCurrentTime.text = formatTime(newPosition)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    progressTimer?.cancel()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    startProgressTimer()
                }
            })

            // 视频准备好后回调
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                binding.tvDuration.text = formatTime(binding.videoView.duration)
            }

            // 视频播放完成回调
            binding.videoView.setOnCompletionListener {
                isPlaying = false
                binding.ivPlayIcon.visibility = View.VISIBLE
            }

            // 视频错误回调
            binding.videoView.setOnErrorListener { _, _, _ ->
                binding.ivCover.visibility = View.VISIBLE
                binding.ivPlayIcon.visibility = View.VISIBLE
                true
            }

            // 设置视频路径
            val videoUrl = video.getBestVideoUrl()
            if (!videoUrl.isNullOrEmpty()) {
                binding.videoView.setVideoPath(videoUrl)
            }
        }

        fun playVideo() {
            val videoUrl = video?.getBestVideoUrl()
            if (videoUrl.isNullOrEmpty()) return

            binding.ivCover.visibility = View.GONE
            binding.ivPlayIcon.visibility = View.GONE
            binding.videoView.start()
            isPlaying = true
            startProgressTimer()
        }

        fun pauseVideo() {
            if (binding.videoView.isPlaying) {
                binding.videoView.pause()
            }
            isPlaying = false
            binding.ivPlayIcon.visibility = View.VISIBLE
            stopProgressTimer()
        }

        private fun startProgressTimer() {
            stopProgressTimer()
            progressTimer = Timer().apply {
                schedule(progressUpdateTask, 0, 100)
            }
        }

        private fun stopProgressTimer() {
            progressTimer?.cancel()
            progressTimer = null
        }

        private fun updateProgress() {
            if (!binding.videoView.isPlaying) return

            val currentPosition = binding.videoView.currentPosition
            val duration = binding.videoView.duration
            if (duration > 0) {
                val progress = (currentPosition * 100f / duration).roundToInt()
                binding.seekBarProgress.progress = progress
                binding.tvCurrentTime.text = formatTime(currentPosition)
            }
        }

        private fun formatTime(milliseconds: Int): String {
            val seconds = milliseconds / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%02d:%02d", minutes, remainingSeconds)
        }

        private fun formatCount(count: Int): String {
            return when {
                count >= 10000 -> String.format("%.1fw", count / 10000f)
                count >= 1000 -> String.format("%.1fw", count / 1000f)
                else -> count.toString()
            }
        }

        fun release() {
            stopProgressTimer()
            binding.videoView.stopPlayback()
            isPlaying = false
        }
    }

    fun release() {
        currentPlayingHolder?.release()
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