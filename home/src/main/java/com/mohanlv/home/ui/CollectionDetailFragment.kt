package com.mohanlv.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.getStatusBarHeight
import com.mohanlv.common.getSafeContext
import com.mohanlv.home.api.PexelsApiClient
import com.mohanlv.home.databinding.FragmentCollectionDetailBinding
import com.mohanlv.home.logE
import com.mohanlv.home.navigateToDetail
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import kotlinx.coroutines.launch

/**
 * 合集详情页
 */
@Route(path = "oneandroid://home/collection/detail", description = "合集详情")
class CollectionDetailFragment : BaseFragment<FragmentCollectionDetailBinding>() {

    companion object {
        private const val EXTRA_COLLECTION_ID = "collection_id"
        private const val EXTRA_COLLECTION_TITLE = "collection_title"
    }

    private val apiService = PexelsApiClient.apiService
    private val mediaAdapter = CollectionMediaAdapter { media ->
        navigateToDetail(media)
    }

    private var collectionId: String? = null
    private var collectionTitle: String? = null
    private var isLoading = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCollectionDetailBinding {
        return FragmentCollectionDetailBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.toolbar.updatePadding(top = getStatusBarHeight(getSafeContext()))
        binding.toolbar.setNavigationOnClickListener {
            RouterManager.popBackStack()
        }

        collectionId = arguments?.getString(EXTRA_COLLECTION_ID)
        collectionTitle = arguments?.getString(EXTRA_COLLECTION_TITLE)
        binding.toolbar.title = collectionTitle ?: ""

        setupRecyclerView()
        setupRetry()
    }

    override fun initData() {
        super.initData()
        loadMedia()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = mediaAdapter
        }
    }

    private fun setupRetry() {
        binding.layoutError.findViewById<View>(com.mohanlv.home.R.id.btnRetry)?.setOnClickListener {
            loadMedia()
        }
    }

    private fun loadMedia() {
        val id = collectionId ?: return
        if (isLoading) return
        isLoading = true

        showLoadingView()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getCollectionMedia(id = id, page = 1, perPage = 20)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val mediaList = body.media ?: emptyList()
                        if (mediaList.isEmpty()) {
                            showEmptyView()
                        } else {
                            showContentView()
                            mediaAdapter.submitList(mediaList)
                        }
                    } else {
                        showEmptyView()
                    }
                } else {
                    logE("CollectionDetailFragment::加载失败 code=${response.code()}")
                    showErrorView()
                }
            } catch (e: Exception) {
                logE("CollectionDetailFragment::加载异常", e)
                showErrorView()
            } finally {
                isLoading = false
            }
        }
    }

    private fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showContentView() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    private fun showErrorView() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
    }
}