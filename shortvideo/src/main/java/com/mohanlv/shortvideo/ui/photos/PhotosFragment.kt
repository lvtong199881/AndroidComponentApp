package com.mohanlv.shortvideo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.router.RouterManager
import com.mohanlv.shortvideo.api.PexelsApiClient
import com.mohanlv.shortvideo.databinding.FragmentPhotosBinding
import com.mohanlv.shortvideo.model.Photo
import kotlinx.coroutines.launch

/**
 * 精选照片页面
 */
class PhotosFragment : BaseFragment<FragmentPhotosBinding>() {

    private val photos = mutableListOf<Photo>()
    private lateinit var photosAdapter: PhotosAdapter

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPhotosBinding {
        return FragmentPhotosBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupWindowInsets()
        setupRecyclerView()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun initData() {
        super.initData()
        loadPhotos()
    }

    private fun setupRecyclerView() {
        photosAdapter = PhotosAdapter(
            photos = photos,
            onItemClick = { _ ->
                // 点击照片事件
            },
            onSearchClick = {
                RouterManager.navigate("oneandroid://shortvideo/photos/search")
            }
        )

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = photosAdapter

            // 搜索栏占满整行，照片保持2列
            val manager = layoutManager as StaggeredGridLayoutManager
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
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
                        photosAdapter.notifyPhotosChanged()
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
                        photos.addAll(body.photos)
                        photosAdapter.addPhotos(body.photos)
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