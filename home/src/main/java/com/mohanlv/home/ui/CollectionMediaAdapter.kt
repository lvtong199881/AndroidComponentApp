package com.mohanlv.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.home.databinding.ItemCollectionMediaBinding
import com.mohanlv.home.model.MediaItem

/**
 * 合集媒体适配器（混合 photo 和 video）
 */
class CollectionMediaAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<CollectionMediaViewHolder>() {

    private val mediaList = mutableListOf<MediaItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionMediaViewHolder {
        val binding = ItemCollectionMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionMediaViewHolder(binding) { media ->
            onItemClick(media)
        }
    }

    override fun onBindViewHolder(holder: CollectionMediaViewHolder, position: Int) {
        holder.bind(mediaList[position])
    }

    override fun getItemCount(): Int = mediaList.size

    fun submitList(newList: List<MediaItem>) {
        mediaList.clear()
        mediaList.addAll(newList)
        notifyDataSetChanged()
    }
}