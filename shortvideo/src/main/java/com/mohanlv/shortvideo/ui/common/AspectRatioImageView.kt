package com.mohanlv.shortvideo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import coil.load
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.ItemPhotoBinding
import com.mohanlv.shortvideo.model.Photo

/**
 * 根据图片宽高比动态计算高度的 ImageView
 */
class AspectRatioImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var aspectRatio: Float = 1f

    /**
     * 设置宽高比
     * @param width 宽度
     * @param height 高度
     */
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