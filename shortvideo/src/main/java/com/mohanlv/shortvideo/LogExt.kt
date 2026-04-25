package com.mohanlv.shortvideo

import android.util.Log

internal fun logE(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Log.e("ShortVideo", message, throwable)
    } else {
        Log.e("ShortVideo", message)
    }
}
