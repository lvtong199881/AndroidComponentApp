package com.mohanlv.shortvideo.ui.shortvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.api.PexelsApiClient
import com.mohanlv.shortvideo.databinding.FragmentVideosBinding
import com.mohanlv.shortvideo.model.Video
import com.mohanlv.shortvideo.ui.DiscoverVideoAdapter
import kotlinx.coroutines.launch

/**
 * 热门视频页面
 */
@Route(path = "oneandroid://shortvideo/videos", description = "热门视频")
class VideosFragment : BaseFragment<FragmentVideosBinding>() {

    private val videos = mutableListOf<Video>()
    private val videoAdapter: DiscoverVideoAdapter by lazy {
        DiscoverVideoAdapter(videos) { _ ->
            // 点击视频事件
        }
    }

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentVideosBinding {
        return FragmentVideosBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRecyclerView()
    }

    override fun initData() {
        super.initData()
        loadVideos()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = videoAdapter

            // 加载更多
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                    if (lastVisibleItem >= totalItemCount - 3 && !isLoading && hasMoreData) {
                        loadMoreVideos()
                    }
                }
            })
        }
    }

    private fun loadVideos() {
        if (isLoading) return
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = PexelsApiClient.apiService.getPopularVideos(page = 1, perPage = 15)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.videos.isNullOrEmpty()) {
                        videos.clear()
                        videos.addAll(body.videos)
                        videoAdapter.notifyDataSetChanged()
                        currentPage = 1
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                        binding.layoutEmpty.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                } else {
                    Logger.e("VideosFragment", "加载视频失败: ${response.code()}")
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Logger.e("VideosFragment", "加载视频异常", e)
                binding.layoutEmpty.visibility = View.VISIBLE
            } finally {
                isLoading = false
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadMoreVideos() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = PexelsApiClient.apiService.getPopularVideos(page = nextPage, perPage = 15)
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
                Logger.e("VideosFragment", "加载更多视频异常", e)
            } finally {
                isLoading = false
            }
        }
    }
}