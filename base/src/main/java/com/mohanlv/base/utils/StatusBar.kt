package com.mohanlv.base.utils

import android.content.Context
import android.util.DisplayMetrics

fun getStatusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    return if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else {
        dpToPx(25, context)
    }
}

fun dpToPx(dp: Int, context: Context): Int {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT).toInt()
}