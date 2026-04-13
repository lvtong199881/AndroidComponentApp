package com.mohanlv.home.ui

import android.os.Bundle
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mohanlv.base.R
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.ImageLoader
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.network.model.Article
import com.mohanlv.network.model.Banner
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.home.databinding.FragmentHomeBinding
import com.mohanlv.home.databinding.ItemBannerBinding
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

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
                e.printStackTrace()
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
        
        for (i in banners.indices) {
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
            scheduleAtFixedRate(object : TimerTask() {
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

/**
 * Banner 适配器
 */
class BannerAdapter(
    private val banners: List<Banner>,
    private val onClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.binding.tvTitle.text = banner.title
        ImageLoader.load(holder.binding.ivBanner, banner.imagePath)
        holder.itemView.setOnClickListener { onClick(banner) }
    }

    override fun getItemCount() = banners.size
}

/**
 * 文章列表适配器
 */
class ArticleAdapter(
    private val articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: android.widget.TextView = itemView.findViewById(R.id.tvTitle)
        val authorText: android.widget.TextView = itemView.findViewById(R.id.tvAuthor)
        val dateText: android.widget.TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.titleText.text = article.title
        holder.authorText.text = article.author.ifEmpty { article.shareUser }
        holder.dateText.text = article.niceDate
        holder.itemView.setOnClickListener { onItemClick(article) }
    }

    override fun getItemCount() = articles.size
}

/**
 * ViewPager 深度切换动画
 */
class DepthPageTransformer : ViewPager2.PageTransformer {
    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        when {
            position < -1 -> page.alpha = 0f
            position <= 0 -> {
                page.alpha = 1f
                page.translationX = 0f
                page.translationZ = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> {
                page.alpha = 1 - position
                page.translationX = pageWidth * -position
                page.translationZ = -1f
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> page.alpha = 0f
        }
    }
}
