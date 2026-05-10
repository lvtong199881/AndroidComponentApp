package com.mohanlv.shortvideo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import coil.load
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.getStatusBarHeight
import com.mohanlv.common.getSafeContext
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.FragmentPvDetailBinding

/**
 * 图片/视频详情页面
 */
@Route(path = "oneandroid://shortvideo/detail", description = "详情")
class PVDetailFragment : BaseFragment<FragmentPvDetailBinding>() {

    companion object {
        private const val EXTRA_TYPE = "type"
        private const val EXTRA_PHOTO_URL = "photo_url"
        private const val EXTRA_VIDEO_URL = "video_url"
        private const val EXTRA_IMAGE_URL = "image_url"
        private const val EXTRA_AUTHOR = "author"
        private const val TYPE_PHOTO = "photo"
        private const val TYPE_VIDEO = "video"

        fun navigateToPhoto(photoUrl: String, imageUrl: String, author: String? = null) {
            RouterManager.navigate(
                "oneandroid://shortvideo/detail",
                Bundle().apply {
                    putString(EXTRA_TYPE, TYPE_PHOTO)
                    putString(EXTRA_PHOTO_URL, photoUrl)
                    putString(EXTRA_IMAGE_URL, imageUrl)
                    putString(EXTRA_AUTHOR, author)
                }
            )
        }

        fun navigateToVideo(videoUrl: String, imageUrl: String, author: String? = null) {
            RouterManager.navigate(
                "oneandroid://shortvideo/detail",
                Bundle().apply {
                    putString(EXTRA_TYPE, TYPE_VIDEO)
                    putString(EXTRA_VIDEO_URL, videoUrl)
                    putString(EXTRA_IMAGE_URL, imageUrl)
                    putString(EXTRA_AUTHOR, author)
                }
            )
        }
    }

    private var isMuted = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPvDetailBinding {
        return FragmentPvDetailBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            RouterManager.popBackStack()
        }
        binding.toolbar.updatePadding(top = getStatusBarHeight(getSafeContext()))

        arguments?.let { args ->
            val type = args.getString(EXTRA_TYPE)
            val imageUrl = args.getString(EXTRA_IMAGE_URL, "")
            val author = args.getString(EXTRA_AUTHOR, "")

            binding.toolbar.title = author

            when (type) {
                TYPE_PHOTO -> {
                    val photoUrl = args.getString(EXTRA_PHOTO_URL, "")
                    showPhoto(photoUrl)
                }
                TYPE_VIDEO -> {
                    val videoUrl = args.getString(EXTRA_VIDEO_URL, "")
                    showVideo(videoUrl, imageUrl)
                }
            }
        }
    }

    private fun showPhoto(photoUrl: String) {
        binding.imageContent.visibility = View.VISIBLE
        binding.videoContent.visibility = View.GONE
        binding.btnPlay.visibility = View.GONE
        binding.btnMute.visibility = View.GONE
        binding.imageContent.load(photoUrl) {
            crossfade(true)
            placeholder(R.color.background)
            error(R.color.divider)
        }
    }

    private fun showVideo(videoUrl: String, imageUrl: String) {
        binding.imageContent.visibility = View.VISIBLE
        binding.videoContent.visibility = View.GONE
        binding.btnPlay.visibility = View.VISIBLE
        binding.btnMute.visibility = View.VISIBLE

        binding.btnMute.setOnClickListener { toggleMute() }

        // 显示封面图
        binding.imageContent.load(imageUrl) {
            crossfade(true)
            placeholder(R.color.background)
            error(R.color.divider)
        }

        // 自动播放视频
        playVideo(videoUrl)
    }

    private var mediaPlayer: android.media.MediaPlayer? = null

    private fun playVideo(videoUrl: String) {
        binding.imageContent.visibility = View.GONE
        binding.btnPlay.visibility = View.GONE
        binding.videoContent.visibility = View.VISIBLE

        // 设置 VideoView 居中
        val params = binding.videoContent.layoutParams as android.widget.FrameLayout.LayoutParams
        params.gravity = android.view.Gravity.CENTER
        binding.videoContent.layoutParams = params

        binding.videoContent.setOnClickListener {
            togglePlayPause()
        }

        binding.videoContent.setVideoPath(videoUrl)
        binding.videoContent.setOnPreparedListener { mp ->
            mediaPlayer = mp
            mp.isLooping = true
            mp.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
            mp.start()
        }
        binding.videoContent.start()
    }

    private fun togglePlayPause() {
        if (binding.videoContent.isPlaying) {
            binding.videoContent.pause()
            binding.btnPlay.visibility = View.VISIBLE
        } else {
            binding.videoContent.start()
            binding.btnPlay.visibility = View.GONE
        }
    }

    private fun toggleMute() {
        isMuted = !isMuted
        mediaPlayer?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
        binding.btnMute.setImageResource(
            if (isMuted) android.R.drawable.ic_lock_silent_mode
            else android.R.drawable.ic_lock_silent_mode_off
        )
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoContent.isPlaying) {
            binding.videoContent.pause()
        }
    }

    override fun onDestroyView() {
        binding.videoContent.stopPlayback()
        mediaPlayer = null
        super.onDestroyView()
    }
}