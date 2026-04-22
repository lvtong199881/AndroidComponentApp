package com.mohanlv.network

import com.mohanlv.logger.Logger

private const val TAG = "Network"

internal fun Any.log(message: String = "") {
    Logger.i(TAG, "$message with $this")
}

internal fun logW(message: String) {
    Logger.w(TAG, message)
}

internal fun logE(message: String, throwable: Throwable? = null) {
    Logger.e(TAG, message, throwable)
}
