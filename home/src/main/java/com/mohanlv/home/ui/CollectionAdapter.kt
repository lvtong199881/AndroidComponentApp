package com.mohanlv.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.home.databinding.ItemCollectionBinding
import com.mohanlv.home.model.PexelsCollection

/**
 * 精选合集适配器
 */
class CollectionAdapter(
    private val onItemClick: (PexelsCollection) -> Unit
) : RecyclerView.Adapter<CollectionViewHolder>() {

    private val collections = mutableListOf<PexelsCollection>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding) { collection ->
            onItemClick(collection)
        }
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(collections[position])
    }

    override fun getItemCount(): Int = collections.size

    fun submitList(newCollections: List<PexelsCollection>) {
        collections.clear()
        collections.addAll(newCollections)
        notifyDataSetChanged()
    }
}