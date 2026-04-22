package com.mohanlv.startup

import android.util.Log

private const val TAG = "Startup"

internal fun Any.log(message: String = "") {
    Log.i(TAG, "$message with $this")
}

internal fun logW(message: String) {
    Log.w(TAG, message)
}

internal fun logE(message: String, throwable: Throwable? = null) {
    Log.e(TAG, message, throwable)
}
