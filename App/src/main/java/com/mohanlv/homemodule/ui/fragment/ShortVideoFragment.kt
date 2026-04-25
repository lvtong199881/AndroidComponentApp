package com.mohanlv.homemodule.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mohanlv.basemodule.base.BaseFragment
import com.mohanlv.basemodule.network.Resource
import com.mohanlv.basemodule.util.ToastHelper
import com.mohanlv.homemodule.R
import com.mohanlv.homemodule.adapter.ShortVideoAdapter
import com.mohanlv.homemodule.api.PexelsApiService
import com.mohanlv.homemodule.databinding.FragmentShortVideoBinding
import com.mohanlv.homemodule.model.Video
import kotlinx.coroutines.launch

/**
 * 短视频页面
 */
class ShortVideoFragment : BaseFragment<FragmentShortVideoBinding>() {

    private lateinit var adapter: ShortVideoAdapter
    private val apiService = PexelsApiService()

    private var currentPage = 1
    private val perPage = 10
    private var isLoading = false
    private var hasMoreData = true

    companion object {
        fun newInstance(): ShortVideoFragment {
            return ShortVideoFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_short_video, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initViewPager()
        loadVideos()
    }

    private fun initViewPager() {
        adapter = ShortVideoAdapter()
        adapter.setListener(object : ShortVideoAdapter.OnVideoInteractionListener {
            override fun onLikeClick(video: Video) {
                ToastHelper.show("点赞: ${video.title}")
            }

            override fun onCommentClick(video: Video) {
                ToastHelper.show("评论: ${video.title}")
            }

            override fun onShareClick(video: Video) {
                ToastHelper.show("分享: ${video.title}")
            }
        })

        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.playVideoAt(position)

                // 预加载更多
                if (position >= adapter.itemCount - 3 && !isLoading && hasMoreData) {
                    loadMoreVideos()
                }
            }
        })
    }

    private fun loadVideos() {
        if (isLoading) return
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE

        viewModelScope.launch {
            when (val result = apiService.getPopularVideos(currentPage, perPage)) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (!result.data.isNullOrEmpty()) {
                        adapter.setVideos(result.data)
                        currentPage++
                    } else {
                        hasMoreData = false
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    ToastHelper.show("加载失败: ${result.message}")
                }
                is Resource.Loading -> {
                    // loading
                }
            }
            isLoading = false
        }
    }

    private fun loadMoreVideos() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewModelScope.launch {
            when (val result = apiService.getPopularVideos(currentPage, perPage)) {
                is Resource.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        adapter.addVideos(result.data)
                        currentPage++
                    } else {
                        hasMoreData = false
                    }
                }
                is Resource.Error -> {
                    ToastHelper.show("加载更多失败: ${result.message}")
                }
                is Resource.Loading -> {
                    // loading
                }
            }
            isLoading = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.playVideoAt(binding.viewPager.currentItem)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::adapter.isInitialized) {
            adapter.pauseCurrentVideo()
        }
    }
}