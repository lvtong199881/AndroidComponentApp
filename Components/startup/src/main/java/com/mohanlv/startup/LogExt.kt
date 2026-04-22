package com.mohanlv.startup

import android.util.Log

private const val TAG = "Startup"

/**
 * 模块日志输出，tag 固定为 "Startup"
 * 原 tag 信息会拼接到消息开头
 */
fun log(message: String, throwable: Throwable? = null) {
    logD(message, throwable)
}

fun logD(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Log.d(TAG, message, throwable)
    } else {
        Log.d(TAG, message)
    }
}

fun logI(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Log.i(TAG, message, throwable)
    } else {
        Log.i(TAG, message)
    }
}

fun logW(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Log.w(TAG, message, throwable)
    } else {
        Log.w(TAG, message)
    }
}

fun logE(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Log.e(TAG, message, throwable)
    } else {
        Log.e(TAG, message)
    }
}
