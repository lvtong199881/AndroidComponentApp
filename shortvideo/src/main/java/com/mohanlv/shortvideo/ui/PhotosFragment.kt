package com.mohanlv.shortvideo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.api.PexelsApiClient
import com.mohanlv.shortvideo.databinding.FragmentPhotosBinding
import com.mohanlv.shortvideo.model.Photo
import kotlinx.coroutines.launch

/**
 * 精选照片页面
 */
class PhotosFragment : BaseFragment<FragmentPhotosBinding>() {

    private val photos = mutableListOf<Photo>()
    private lateinit var photoAdapter: PhotoAdapter

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPhotosBinding {
        return FragmentPhotosBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRecyclerView()
    }

    override fun initData() {
        super.initData()
        loadPhotos()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(photos) { photo ->
            // 点击照片事件
        }

        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
            adapter = photoAdapter

            // 加载更多
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItem >= totalItemCount - 3 && !isLoading && hasMoreData) {
                        loadMorePhotos()
                    }
                }
            })
        }
    }

    private fun loadPhotos() {
        if (isLoading) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = PexelsApiClient.apiService.getCuratedPhotos(page = 1, perPage = 20)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.photos.isNullOrEmpty()) {
                        photos.clear()
                        photos.addAll(body.photos)
                        photoAdapter.notifyDataSetChanged()
                        currentPage = 1
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                        binding.layoutEmpty.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                } else {
                    Logger.e("PhotosFragment", "加载照片失败: ${response.code()}")
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Logger.e("PhotosFragment", "加载照片异常", e)
                binding.layoutEmpty.visibility = View.VISIBLE
            } finally {
                isLoading = false
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadMorePhotos() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = PexelsApiClient.apiService.getCuratedPhotos(page = nextPage, perPage = 20)
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
                Logger.e("PhotosFragment", "加载更多照片异常", e)
            } finally {
                isLoading = false
            }
        }
    }
}