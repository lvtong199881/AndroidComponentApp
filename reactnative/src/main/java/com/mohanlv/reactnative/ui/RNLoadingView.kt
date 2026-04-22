package com.mohanlv.reactnative.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mohanlv.reactnative.R

/**
 * React Native 加载视图
 * 显示 loading 进度条 + 文字
 */
class RNLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_rn_loading, this, true)
    }
}
