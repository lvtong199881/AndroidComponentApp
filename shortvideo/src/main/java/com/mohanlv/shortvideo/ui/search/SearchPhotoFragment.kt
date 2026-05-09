package com.mohanlv.shortvideo.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.api.PexelsApiClient
import com.mohanlv.shortvideo.databinding.FragmentSearchPhotoBinding
import com.mohanlv.shortvideo.model.Photo
import kotlinx.coroutines.launch

/**
 * 图片搜索页面
 */
@Route(path = "oneandroid://shortvideo/photos/search", description = "搜索图片")
class SearchPhotoFragment : BaseFragment<FragmentSearchPhotoBinding>() {

    private val photos = mutableListOf<Photo>()
    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(
            photos = photos,
            onItemClick = { _ ->
                // 点击照片事件
            }
        )
    }

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true
    private var currentQuery = ""

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchPhotoBinding {
        return FragmentSearchPhotoBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        // 自动拉起键盘
        binding.editSearch.requestFocus()
        binding.editSearch.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = photoAdapter

            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                    if (lastVisibleItem >= totalItemCount - 3 && !isLoading && hasMoreData) {
                        loadMorePhotos()
                    }
                }
            })
        }
    }

    private fun setupSearchBar() {
        binding.btnBack.setOnClickListener {
            RouterManager.popBackStack()
        }

        binding.btnClear.setOnClickListener {
            binding.editSearch.setText("")
        }

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.editSearch.text.toString().trim()
        if (query.isEmpty()) {
            return
        }
        currentQuery = query
        // 隐藏键盘
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
        searchPhotos(query)
    }

    private fun searchPhotos(query: String) {
        if (isLoading) return
        isLoading = true
        currentPage = 1

        showLoadingView()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = PexelsApiClient.apiService.searchPhotos(query = query, page = 1, perPage = 20)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.photos.isNullOrEmpty()) {
                        photos.clear()
                        photos.addAll(body.photos)
                        photoAdapter.notifyDataSetChanged()
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                        binding.layoutEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    } else {
                        photos.clear()
                        photoAdapter.notifyDataSetChanged()
                        showEmptyView()
                    }
                } else {
                    Logger.e("SearchPhotoFragment", "搜索照片失败: ${response.code()}")
                    showEmptyView()
                }
            } catch (e: Exception) {
                Logger.e("SearchPhotoFragment", "搜索照片异常", e)
                showEmptyView()
            } finally {
                isLoading = false
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadMorePhotos() {
        if (isLoading || !hasMoreData || currentQuery.isEmpty()) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = PexelsApiClient.apiService.searchPhotos(query = currentQuery, page = nextPage, perPage = 20)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.photos.isNullOrEmpty()) {
                        val startPosition = photos.size
                        photos.addAll(body.photos)
                        photoAdapter.notifyItemRangeInserted(startPosition, body.photos.size)
                        currentPage = nextPage
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                    } else {
                        hasMoreData = false
                    }
                }
            } catch (e: Exception) {
                Logger.e("SearchPhotoFragment", "加载更多照片异常", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.visibility = if (photos.isNotEmpty()) View.VISIBLE else View.GONE
    }
}