package com.mohanlv.shortvideo.ui

import android.view.View
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
 * 短视频 ViewHolder
 * 由 ShortVideoAdapter 使用
 */
class VideoViewHolder(
    private val binding: ItemShortVideoBinding,
    private val onLikeClick: (Video) -> Unit,
    private val onCommentClick: (Video) -> Unit,
    private val onShareClick: (Video) -> Unit
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

        ImageLoader.load(binding.ivCover, video.image)
        binding.ivCover.visibility = View.VISIBLE
        binding.ivPlayIcon.visibility = View.VISIBLE

        binding.tvAuthor.text = "@${video.user.name}"
        binding.tvTitle.text = video.url

        binding.seekBarProgress.progress = 0

        binding.tvLikeCount.text = formatCount((1000..50000).random())
        binding.tvCommentCount.text = formatCount((100..10000).random())
        binding.tvShareCount.text = itemView.context.getString(R.string.share)

        binding.ivPlayIcon.setOnClickListener { playVideo() }
        binding.ivCover.setOnClickListener { playVideo() }

        binding.videoView.setOnClickListener {
            if (isPlaying) pauseVideo() else playVideo()
        }

        binding.ivLike.setOnClickListener { onLikeClick(video) }
        binding.tvCommentCount.setOnClickListener { onCommentClick(video) }
        binding.tvShareCount.setOnClickListener { onShareClick(video) }

        binding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = binding.videoView.duration
                    val newPosition = (duration * progress / 100f).roundToInt()
                    binding.videoView.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progressTimer?.cancel()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startProgressTimer()
            }
        })

        binding.videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        binding.videoView.setOnCompletionListener {
            isPlaying = false
            binding.ivPlayIcon.visibility = View.VISIBLE
        }

        binding.videoView.setOnErrorListener { _, _, _ ->
            binding.ivCover.visibility = View.VISIBLE
            binding.ivPlayIcon.visibility = View.VISIBLE
            true
        }

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

    fun release() {
        stopProgressTimer()
        binding.videoView.stopPlayback()
        isPlaying = false
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
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1fw", count / 10000f)
            count >= 1000 -> String.format("%.1fw", count / 1000f)
            else -> count.toString()
        }
    }
}