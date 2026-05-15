package com.mohanlv.home.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 根据图片宽高比动态计算高度的 ImageView
 */
class AspectRatioImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var aspectRatio: Float = 1f

    fun setAspectRatio(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            aspectRatio = width.toFloat() / height.toFloat()
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        if (measuredWidth > 0 && aspectRatio > 0) {
            val calculatedHeight = (measuredWidth / aspectRatio).toInt()
            setMeasuredDimension(measuredWidth, calculatedHeight)
        }
    }
}