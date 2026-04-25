package com.mohanlv.homemodule.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.homemodule.R
import com.mohanlv.homemodule.model.Video

/**
 * 短视频适配器
 */
class ShortVideoAdapter : RecyclerView.Adapter<ShortVideoAdapter.VideoViewHolder>() {

    private val videos = mutableListOf<Video>()
    private var currentPlayingPosition = -1
    private var listener: OnVideoInteractionListener? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    interface OnVideoInteractionListener {
        fun onLikeClick(video: Video)
        fun onCommentClick(video: Video)
        fun onShareClick(video: Video)
    }

    fun setListener(listener: OnVideoInteractionListener) {
        this.listener = listener
    }

    fun setVideos(newVideos: List<Video>) {
        videos.clear()
        videos.addAll(newVideos)
        notifyDataSetChanged()
    }

    fun addVideos(newVideos: List<Video>) {
        val startPos = videos.size
        videos.addAll(newVideos)
        notifyItemRangeInserted(startPos, newVideos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_short_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    fun playVideoAt(position: Int) {
        if (position == currentPlayingPosition) return
        // 暂停上一个
        if (currentPlayingPosition >= 0 && currentPlayingPosition < videos.size) {
            notifyItemChanged(currentPlayingPosition, "pause")
        }
        currentPlayingPosition = position
        notifyItemChanged(position, "play")
    }

    fun pauseCurrentVideo() {
        if (currentPlayingPosition >= 0) {
            notifyItemChanged(currentPlayingPosition, "pause")
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val authorText: TextView = itemView.findViewById(R.id.authorText)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val videoProgress: ProgressBar = itemView.findViewById(R.id.videoProgress)
        private val likeCountText: TextView = itemView.findViewById(R.id.likeCountText)
        private val commentCountText: TextView = itemView.findViewById(R.id.commentCountText)
        private val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        private val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
        private val shareIcon: ImageView = itemView.findViewById(R.id.shareIcon)

        private var isPlaying = false
        private var currentVideo: Video? = null
        private val progressRunnable = object : Runnable {
            override fun run() {
                if (isPlaying && videoView.isPlaying) {
                    val current = videoView.currentPosition
                    val duration = videoView.duration
                    if (duration > 0) {
                        videoProgress.progress = current * 100 / duration
                    }
                    mainHandler.postDelayed(this, 100)
                }
            }
        }

        init {
            videoView.setOnPreparedListener { mp ->
                mp.start()
                coverImage.visibility = View.GONE
                progressBar.visibility = View.GONE
                isPlaying = true
                mainHandler.post(progressRunnable)
            }

            videoView.setOnCompletionListener { mp ->
                mp.start()
            }

            videoView.setOnErrorListener { _, _, _ ->
                progressBar.visibility = View.GONE
                true
            }

            likeIcon.setOnClickListener {
                currentVideo?.let { listener?.onLikeClick(it) }
            }

            commentIcon.setOnClickListener {
                currentVideo?.let { listener?.onCommentClick(it) }
            }

            shareIcon.setOnClickListener {
                currentVideo?.let { listener?.onShareClick(it) }
            }
        }

        fun bind(video: Video) {
            currentVideo = video
            authorText.text = "@${video.authorName}"
            titleText.text = video.title
            likeCountText.text = formatCount(video.likeCount)
            commentCountText.text = formatCount(video.commentCount)
            videoProgress.progress = 0
            coverImage.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }

        fun play() {
            currentVideo?.let {
                coverImage.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                videoView.setVideoURI(Uri.parse(it.videoUrl))
            }
        }

        fun pause() {
            isPlaying = false
            mainHandler.removeCallbacks(progressRunnable)
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }

        private fun formatCount(count: Long): String {
            return when {
                count >= 10000 -> String.format("%.1fw", count / 10000.0)
                else -> count.toString()
            }
        }
    }
}