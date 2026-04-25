package com.mohanlv.shortvideo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.databinding.FragmentShortVideoBinding
import com.mohanlv.shortvideo.model.Video
import com.mohanlv.shortvideo.api.PexelsApiClient
import kotlinx.coroutines.launch

/**
 * 短视频页面
 * 使用 ViewPager2 竖向滑动展示视频
 */
@Route(path = "oneandroid://shortvideo/main", description = "短视频")
class ShortVideoFragment : BaseFragment<FragmentShortVideoBinding>() {

    private val videos = mutableListOf<Video>()
    private lateinit var videoAdapter: ShortVideoAdapter

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    private val apiKey = PexelsApiClient.apiService

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentShortVideoBinding {
        return FragmentShortVideoBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupViewPager()
    }

    override fun initData() {
        super.initData()
        loadVideos()
    }

    private fun setupViewPager() {
        videoAdapter = ShortVideoAdapter(
            videos = videos,
            onLikeClick = { video -> onLikeClicked(video) },
            onCommentClick = { video -> onCommentClicked(video) },
            onShareClick = { video -> onShareClicked(video) }
        )

        binding.viewPagerVideo.apply {
            adapter = videoAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    videoAdapter.setCurrentPosition(position)

                    if (position >= videos.size - 3 && !isLoading && hasMoreData) {
                        loadMoreVideos()
                    }
                }
            })
        }
    }

    private fun loadVideos() {
        if (isLoading) return
        isLoading = true
        showLoading()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiKey.getPopularVideos(page = 1, perPage = 15)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.videos.isNullOrEmpty()) {
                        videos.clear()
                        videos.addAll(body.videos)
                        videoAdapter.notifyDataSetChanged()
                        currentPage = 1
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                        showContent()
                    } else {
                        showEmpty()
                    }
                } else {
                    Logger.e("ShortVideoFragment", "加载视频失败: ${response.code()}")
                    showEmpty()
                }
            } catch (e: Exception) {
                Logger.e("ShortVideoFragment", "加载视频异常", e)
                showEmpty()
            } finally {
                isLoading = false
                hideLoading()
            }
        }
    }

    private fun loadMoreVideos() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = apiKey.getPopularVideos(page = nextPage, perPage = 15)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.videos.isNullOrEmpty()) {
                        val startPosition = videos.size
                        videos.addAll(body.videos)
                        videoAdapter.notifyItemRangeInserted(startPosition, body.videos.size)
                        currentPage = nextPage
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                    } else {
                        hasMoreData = false
                    }
                }
            } catch (e: Exception) {
                Logger.e("ShortVideoFragment", "加载更多视频异常", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun onLikeClicked(video: Video) {
        Toast.makeText(context, "点赞: ${video.user.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onCommentClicked(video: Video) {
        Toast.makeText(context, "评论: ${video.user.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onShareClicked(video: Video) {
        Toast.makeText(context, "分享: ${video.user.name}", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showContent() {
        binding.layoutEmpty.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val currentPos = binding.viewPagerVideo.currentItem
        videoAdapter.setCurrentPosition(currentPos)
    }

    override fun onPause() {
        super.onPause()
        videoAdapter.releaseAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoAdapter.release()
    }

    companion object {
        fun newInstance(): ShortVideoFragment {
            return ShortVideoFragment()
        }
    }
}
