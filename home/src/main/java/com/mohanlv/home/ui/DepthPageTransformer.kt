package com.mohanlv.home.ui

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * ViewPager 深度切换动画
 */
class DepthPageTransformer : ViewPager2.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        when {
            position < -1 -> page.alpha = 0f
            position <= 0 -> {
                page.alpha = 1f
                page.translationX = 0f
                page.translationZ = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> {
                page.alpha = 1 - position
                page.translationX = pageWidth * -position
                page.translationZ = -1f
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> page.alpha = 0f
        }
    }
}