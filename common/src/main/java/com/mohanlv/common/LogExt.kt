package com.mohanlv.common

import com.mohanlv.logger.Logger

private const val TAG = "Common"

internal fun Any.log(message: String = "") {
    Logger.i(TAG, "$message with $this")
}

internal fun logW(message: String) {
    Logger.w(TAG, message)
}

fun logE(message: String, throwable: Throwable? = null) {
    Logger.e(TAG, message, throwable)
}
