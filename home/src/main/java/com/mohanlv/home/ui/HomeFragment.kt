package com.mohanlv.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.getStatusBarHeight
import com.mohanlv.common.getSafeContext
import com.mohanlv.home.databinding.FragmentHomeBinding
import com.mohanlv.home.logE
import com.mohanlv.home.api.PexelsApiClient
import com.mohanlv.home.model.PexelsCollection
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import kotlinx.coroutines.launch

/**
 * 首页 - 精选合集
 */
@Route(path = "oneandroid://home/main", description = "首页")
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val pexelsApi = PexelsApiClient.apiService

    private val collections = mutableListOf<PexelsCollection>()
    private var currentPage = 1
    private var isLoading = false

    private lateinit var collectionAdapter: CollectionAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.root.updatePadding(top = getStatusBarHeight(getSafeContext()))
        setupRecyclerView()
        setupSwipeRefresh()
    }

    override fun initData() {
        super.initData()
        loadCollections()
    }

    private fun setupRecyclerView() {
        collectionAdapter = CollectionAdapter { collection ->
            collection.id?.let { id ->
                RouterManager.navigate(
                    "oneandroid://home/collection/detail",
                    Bundle().apply {
                        putString("collection_id", id)
                        putString("collection_title", collection.title ?: "")
                    }
                )
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = collectionAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 1
            collections.clear()
            loadCollections()
        }
    }

    private fun loadCollections() {
        if (isLoading) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = pexelsApi.getFeaturedCollections(page = currentPage, perPage = 20)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val newCollections = body.collections ?: emptyList()
                        if (currentPage == 1) {
                            collections.clear()
                        }
                        collections.addAll(newCollections)
                        collectionAdapter.submitList(collections.toList())
                    } else {
                        logE("HomeFragment::body 为空")
                    }
                } else {
                    logE("HomeFragment::获取精选合集失败 code=${response.code()}")
                }
            } catch (e: Exception) {
                logE("HomeFragment::获取精选合集异常", e)
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}