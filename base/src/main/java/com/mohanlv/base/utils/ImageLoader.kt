package com.mohanlv.base.utils

import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation

/**
 * 图片加载工具
 * 使用 Coil 加载图片
 */
object ImageLoader {

    /**
     * 加载网络图片
     */
    fun load(imageView: ImageView, url: String?) {
        imageView.load(url) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_gallery)
        }
    }

    /**
     * 加载圆角图片
     */
    fun loadRound(imageView: ImageView, url: String?, radius: Float = 8f) {
        imageView.load(url) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_gallery)
            transformations(RoundedCornersTransformation(radius))
        }
    }
}
