package com.mohanlv.home.ui

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.home.databinding.ItemCollectionBinding
import com.mohanlv.home.model.PexelsCollection

/**
 * 精选合集 ViewHolder
 */
class CollectionViewHolder(
    private val binding: ItemCollectionBinding,
    private val onItemClick: (PexelsCollection) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var currentCollection: PexelsCollection? = null

    private val privateBackground: GradientDrawable = GradientDrawable().apply {
        setColor("#FFE4E4".toColorInt())
        cornerRadius = 4f
    }

    init {
        binding.root.setOnClickListener {
            currentCollection?.let { onItemClick(it) }
        }
    }

    fun bind(collection: PexelsCollection) {
        currentCollection = collection
        binding.tvTitle.text = collection.title ?: ""
        if (collection.isPrivate == true) {
            binding.tvPrivate.visibility = View.VISIBLE
            binding.tvPrivate.background = privateBackground
        } else {
            binding.tvPrivate.visibility = View.GONE
        }
        binding.tvDescription.text = collection.description ?: ""
        binding.tvDescription.visibility = if (collection.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.tagMediaCount.setTagText("media ${collection.mediaCount}")
        binding.tagPhotosCount.setTagText("photos ${collection.photosCount}")
        binding.tagVideosCount.setTagText("videos ${collection.videosCount}")
    }
}