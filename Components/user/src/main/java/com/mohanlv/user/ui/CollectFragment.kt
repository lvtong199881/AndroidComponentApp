package com.mohanlv.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.login.vm.LoginState
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.network.model.Article
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.user.R
import com.mohanlv.user.databinding.FragmentCollectBinding
import kotlinx.coroutines.launch

/**
 * 我的收藏页面
 */
@Route(path = RoutePath.COLLECT_LIST, description = "我的收藏")
class CollectFragment : BaseFragment<FragmentCollectBinding>() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)
    private val articles = mutableListOf<Article>()
    private var currentPage = 0
    private var isLoading = false

    companion object {
        private const val TAG = "CollectFragment"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCollectBinding {
        return FragmentCollectBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        
        // 检查登录状态
        if (!LoginState.isLoggedIn) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
            RouterManager.navigate(RoutePath.LOGIN)
            return
        }
        
        setupToolbar()
        setupRecyclerView()
    }

    override fun initData() {
        super.initData()
        loadCollectList()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = ArticleAdapter(articles) { article ->
            // 点击文章，打开链接
            RouterManager.navigate("${RoutePath.WEB_VIEW}?url=${article.link}")
        }
        
        // 加载更多
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                
                if (!isLoading && lastVisibleItem >= totalItemCount - 3) {
                    currentPage++
                    loadCollectList()
                }
            }
        })
        
        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 0
            articles.clear()
            loadCollectList()
        }
    }

    private fun loadCollectList() {
        if (isLoading) return
        isLoading = true
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getCollectList(currentPage)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess()) {
                        val newArticles = body.data?.datas ?: emptyList()
                        if (currentPage == 0) {
                            articles.clear()
                        }
                        articles.addAll(newArticles)
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                        
                        // 更新空状态
                        binding.layoutEmpty.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Log.e(TAG, "获取收藏失败: ${body?.errorMsg}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取收藏失败: ${e.message}")
                Toast.makeText(context, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
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
