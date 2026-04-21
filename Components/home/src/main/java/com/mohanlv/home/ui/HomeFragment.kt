package com.mohanlv.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.home.databinding.FragmentHomeBinding
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.network.model.Article
import com.mohanlv.network.model.Banner
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

/**
 * 首页
 */
@Route(path = RoutePath.HOME, description = "首页")
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    private val banners = mutableListOf<Banner>()
    private val articles = mutableListOf<Article>()
    private var currentPage = 0
    private var isLoading = false

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var articleAdapter: ArticleAdapter

    private var autoScrollTimer: Timer? = null

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
    }

    override fun initData() {
        super.initData()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    private fun setupRecyclerView() {
        // 文章列表
        articleAdapter = ArticleAdapter(articles) { article ->
            RouterManager.navigate(RoutePath.WEB_VIEW, Bundle().apply { putString("url", article.link) })
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = articleAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 0
            articles.clear()
            loadData()
        }
    }

    private fun loadData() {
        if (isLoading) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 并行加载 banner 和文章
                val bannerDeferred = apiService.getBanner()
                val articleDeferred = apiService.getArticleList(currentPage)

                // 处理 Banner
                if (bannerDeferred.isSuccessful) {
                    val body = bannerDeferred.body()
                    if (body != null && body.isSuccess()) {
                        banners.clear()
                        banners.addAll(body.data ?: emptyList())
                        setupBanner()
                    }
                }

                // 处理文章列表
                if (articleDeferred.isSuccessful) {
                    val body = articleDeferred.body()
                    if (body != null && body.isSuccess()) {
                        val newArticles = body.data?.datas ?: emptyList()
                        if (currentPage == 0) {
                            articles.clear()
                        }
                        articles.addAll(newArticles)
                        articleAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取 Banner/文章失败", e)
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun setupBanner() {
        if (banners.isEmpty()) return
        
        bannerAdapter = BannerAdapter(banners) { banner ->
            RouterManager.navigate(RoutePath.WEB_VIEW, Bundle().apply { putString("url", banner.url) })
        }
        binding.viewPagerBanner.adapter = bannerAdapter
        
        // 设置指示器
        setupIndicator()
        
        // Banner 切换监听
        binding.viewPagerBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
        
        // 设置页面切换动画
        binding.viewPagerBanner.setPageTransformer(DepthPageTransformer())
    }

    private fun setupIndicator() {
        binding.layoutIndicator.removeAllViews()
        val size = 8.dp
        val margin = 4.dp
        
        banners.indices.forEach { _ ->
            val dot = ImageView(context).apply {
                setImageDrawable(ContextCompat.getDrawable(requireContext(), android.R.drawable.presence_invisible))
                val params = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(margin, 0, margin, 0)
                }
                layoutParams = params
            }
            binding.layoutIndicator.addView(dot)
        }
        updateIndicator(0)
    }

    private fun updateIndicator(position: Int) {
        val count = binding.layoutIndicator.childCount
        for (i in 0 until count) {
            val imageView = binding.layoutIndicator.getChildAt(i) as ImageView
            imageView.setImageDrawable(ContextCompat.getDrawable(
                requireContext(),
                if (i == position) android.R.drawable.presence_online else android.R.drawable.presence_invisible
            ))
        }
    }

    private fun startAutoScroll() {
        if (banners.size <= 1) return
        stopAutoScroll()
        autoScrollTimer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    activity?.runOnUiThread {
                        val next = (binding.viewPagerBanner.currentItem + 1) % banners.size
                        binding.viewPagerBanner.currentItem = next
                    }
                }
            }, 3000, 3000)
        }
    }

    private fun stopAutoScroll() {
        autoScrollTimer?.cancel()
        autoScrollTimer = null
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }
}
