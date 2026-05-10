package com.mohanlv.shortvideo.ui.detail

import android.os.Build
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
import com.mohanlv.shortvideo.model.Photo
import com.mohanlv.shortvideo.model.Video

/**
 * 图片/视频详情页面
 */
@Route(path = "oneandroid://shortvideo/detail", description = "详情")
class PVDetailFragment : BaseFragment<FragmentPvDetailBinding>() {

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

        val photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("extra_photo", Photo::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("extra_photo")
        }

        val video = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("extra_video", Video::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("extra_video")
        }

        if (video != null) {
            showVideo(video)
        } else if (photo != null) {
            showPhoto(photo)
        }
    }

    private fun showPhoto(photo: Photo) {
        binding.toolbar.title = photo.photographer
        binding.imageContent.visibility = View.VISIBLE
        binding.videoContent.visibility = View.GONE
        binding.btnPlay.visibility = View.GONE
        binding.btnMute.visibility = View.GONE
        binding.imageContent.load(photo.src.large) {
            crossfade(true)
            placeholder(R.color.background)
            error(R.color.divider)
        }
    }

    private fun showVideo(video: Video) {
        binding.toolbar.title = video.user.name
        binding.imageContent.visibility = View.VISIBLE
        binding.videoContent.visibility = View.GONE
        binding.btnPlay.visibility = View.VISIBLE
        binding.btnMute.visibility = View.VISIBLE

        binding.btnMute.setOnClickListener { toggleMute() }

        binding.imageContent.load(video.image) {
            crossfade(true)
            placeholder(R.color.background)
            error(R.color.divider)
        }

        video.getBestVideoUrl()?.let { playVideo(it) }
    }

    private var mediaPlayer: android.media.MediaPlayer? = null

    private fun playVideo(videoUrl: String) {
        binding.imageContent.visibility = View.GONE
        binding.btnPlay.visibility = View.GONE
        binding.videoContent.visibility = View.VISIBLE

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