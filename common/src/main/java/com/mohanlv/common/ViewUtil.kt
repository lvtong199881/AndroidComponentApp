package com.mohanlv.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.fragment.app.Fragment
import com.mohanlv.base.utils.AppUtils

fun Fragment.getSafeContext(): Context {
    return context ?: AppUtils.getContext()
}

fun View.updateMarginTop(@Px top: Int) {
    val lp = this.layoutParams as? ViewGroup.MarginLayoutParams
    lp?.topMargin = top
    this.layoutParams = lp
}

fun View.updateMarginBottom(@Px bottom: Int) {
    val lp = this.layoutParams as? ViewGroup.MarginLayoutParams
    lp?.bottomMargin = bottom
    this.layoutParams = lp
}

fun View.updateMarginLeft(@Px left: Int) {
    val lp = this.layoutParams as? ViewGroup.MarginLayoutParams
    lp?.leftMargin = left
    this.layoutParams = lp
}

fun View.updateMarginRight(@Px right: Int) {
    val lp = this.layoutParams as? ViewGroup.MarginLayoutParams
    lp?.rightMargin = right
    this.layoutParams = lp
}
