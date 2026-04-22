package com.mohanlv.reactnative.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import com.mohanlv.reactnative.R

/**
 * React Native 错误视图
 * 显示错误图标 + 文字 + 重试按钮
 */
class RNErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onRetryListener: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_rn_error, this, true)
        findViewById<Button>(R.id.btn_retry)?.setOnClickListener {
            onRetryListener?.invoke()
        }
    }

    fun setOnRetryListener(listener: () -> Unit) {
        onRetryListener = listener
    }
}
