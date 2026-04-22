package com.mohanlv.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.base.utils.ImageLoader
import com.mohanlv.home.databinding.ItemBannerBinding
import com.mohanlv.network.model.Banner

/**
 * Banner 适配器
 */
class BannerAdapter(
    private val banners: List<Banner>,
    private val onClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.binding.tvTitle.text = banner.title
        ImageLoader.load(holder.binding.ivBanner, banner.imagePath)
        holder.itemView.setOnClickListener { onClick(banner) }
    }

    override fun getItemCount(): Int = banners.size
}