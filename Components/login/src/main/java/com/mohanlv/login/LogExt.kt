package com.mohanlv.login

import com.mohanlv.logger.Logger

private const val TAG = "Login"

internal fun log(message: String) {
    Logger.i(TAG, message)
}

internal fun logW(message: String) {
    Logger.w(TAG, message)
}

internal fun logE(message: String, throwable: Throwable? = null) {
    Logger.e(TAG, message, throwable)
}
