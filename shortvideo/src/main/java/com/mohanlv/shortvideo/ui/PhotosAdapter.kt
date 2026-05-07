package com.mohanlv.shortvideo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohanlv.shortvideo.databinding.ItemSearchHeaderBinding

/**
 * 带搜索栏头部的照片列表适配器
 */
class PhotosAdapter(
    private val photos: MutableList<com.mohanlv.shortvideo.model.Photo>,
    private val onItemClick: (com.mohanlv.shortvideo.model.Photo) -> Unit,
    private val onSearchClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_PHOTO = 1
    }

    private val photoAdapter = PhotoAdapter(
        photos = photos,
        onItemClick = onItemClick
    )

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_PHOTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemSearchHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchHeaderViewHolder(binding)
            }
            else -> photoAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchHeaderViewHolder -> {
                // 搜索栏占满整行
                val layoutParams = holder.binding.root.layoutParams as? StaggeredGridLayoutManager.LayoutParams
                layoutParams?.isFullSpan = true
                holder.binding.root.setOnClickListener { onSearchClick() }
            }
            is PhotoViewHolder -> {
                photoAdapter.onBindViewHolder(holder, position - 1)
            }
        }
    }

    override fun getItemCount(): Int = photos.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (holder) {
            is PhotoViewHolder -> {
                photoAdapter.onBindViewHolder(holder, position - 1, payloads)
            }
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun notifyPhotosChanged() {
        notifyItemRangeChanged(1, photos.size)
    }

    fun addPhotos(newPhotos: List<com.mohanlv.shortvideo.model.Photo>) {
        val startPosition = photos.size + 1
        photos.addAll(newPhotos)
        notifyItemRangeInserted(startPosition, newPhotos.size)
    }

    private class SearchHeaderViewHolder(val binding: ItemSearchHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}