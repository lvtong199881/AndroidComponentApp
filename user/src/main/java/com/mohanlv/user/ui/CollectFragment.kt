package com.mohanlv.user.ui

import android.os.Bundle
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
import com.mohanlv.user.logE
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

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCollectBinding {
        return FragmentCollectBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        
        // 检查登录状态
        if (!LoginState.isLoggedIn) {
            Toast.makeText(context, R.string.please_login_first, Toast.LENGTH_SHORT).show()
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
            RouterManager.navigate(RoutePath.WEB_VIEW, Bundle().apply { putString("url", article.link) })
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

                        activity?.runOnUiThread {
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                            binding.layoutEmpty.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
                        }
                    } else {
                        logE("CollectFragment::API错误: ${body?.errorMsg}")
                    }
                } else {
                    logE("CollectFragment::HTTP错误: ${response.code()}")
                }
            } catch (e: Exception) {
                logE("CollectFragment::加载收藏列表失败", e)
                Toast.makeText(context, getString(R.string.load_failed, e.message), Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}
