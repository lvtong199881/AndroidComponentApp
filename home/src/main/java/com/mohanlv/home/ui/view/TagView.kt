package com.mohanlv.home.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import com.mohanlv.home.R

/**
 * 媒体数量标签 View
 * 支持 xml 配置属性
 */
class TagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rectF = RectF()

    private var tagText: String = ""
    private var tagBackgroundColor: Int = 0
    private var tagBorderColor: Int = 0
    private var tagTextColor: Int = 0
    private var tagCornerRadius: Float = 0f
    private var tagTextSize: Float = 0f
    private var tagHorizontalPadding: Float = 0f
    private var tagVerticalPadding: Float = 0f
    private var tagBorderWidth: Float = 0f

    init {
        // 默认值
        tagBackgroundColor = "#00000000".toColorInt()
        tagBorderColor = "#000000".toColorInt()
        tagTextColor = "#666666".toColorInt()
        tagCornerRadius = 8f.dpToPx()
        tagTextSize = 12f.spToPx()
        tagHorizontalPadding = 8f.dpToPx()
        tagVerticalPadding = 4f.dpToPx()
        tagBorderWidth = 1f.dpToPx()

        backgroundPaint.style = Paint.Style.FILL
        borderPaint.style = Paint.Style.STROKE
        textPaint.textAlign = Paint.Align.CENTER

        // 解析 xml 属性
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TagView)
            try {
                tagText = typedArray.getString(R.styleable.TagView_tagText) ?: ""
                tagBackgroundColor = typedArray.getColor(R.styleable.TagView_tagBackgroundColor, tagBackgroundColor)
                tagBorderColor = typedArray.getColor(R.styleable.TagView_tagBorderColor, tagBorderColor)
                tagTextColor = typedArray.getColor(R.styleable.TagView_tagTextColor, tagTextColor)
                tagCornerRadius = typedArray.getDimension(R.styleable.TagView_tagCornerRadius, tagCornerRadius)
                tagTextSize = typedArray.getDimension(R.styleable.TagView_tagTextSize, tagTextSize)
                tagBorderWidth = typedArray.getDimension(R.styleable.TagView_tagBorderWidth, tagBorderWidth)
            } finally {
                typedArray.recycle()
            }
        }

        backgroundPaint.color = tagBackgroundColor
        borderPaint.color = tagBorderColor
        borderPaint.strokeWidth = tagBorderWidth
        textPaint.textSize = tagTextSize
        textPaint.color = tagTextColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textWidth = textPaint.measureText(tagText)
        val desiredWidth = (textWidth + tagHorizontalPadding * 2).toInt()
        val desiredHeight = (tagTextSize + tagVerticalPadding * 2).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = tagBorderWidth / 2
        rectF.set(halfBorder, halfBorder, width - halfBorder, height - halfBorder)

        canvas.drawRoundRect(rectF, tagCornerRadius, tagCornerRadius, backgroundPaint)
        canvas.drawRoundRect(rectF, tagCornerRadius, tagCornerRadius, borderPaint)

        val centerX = width / 2f
        val centerY = height / 2f
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(tagText, centerX, textY, textPaint)
    }

    fun setTagText(text: String) {
        tagText = text
        requestLayout()
        invalidate()
    }

    fun setTagBackgroundColor(@ColorInt color: Int) {
        tagBackgroundColor = color
        backgroundPaint.color = color
        invalidate()
    }

    fun setTagBorderColor(@ColorInt color: Int) {
        tagBorderColor = color
        borderPaint.color = color
        invalidate()
    }

    fun setTagTextColor(@ColorInt color: Int) {
        tagTextColor = color
        textPaint.color = color
        invalidate()
    }

    fun setTagCornerRadius(radius: Float) {
        tagCornerRadius = radius.dpToPx()
        invalidate()
    }

    fun setTagTextSize(sizeSp: Float) {
        tagTextSize = sizeSp.spToPx()
        textPaint.textSize = tagTextSize
        requestLayout()
        invalidate()
    }

    fun setTagBorderWidth(widthDp: Float) {
        tagBorderWidth = widthDp.dpToPx()
        borderPaint.strokeWidth = tagBorderWidth
        invalidate()
    }

    private fun Float.dpToPx(): Float = this * context.resources.displayMetrics.density

    private fun Float.spToPx(): Float = this * context.resources.displayMetrics.scaledDensity
}