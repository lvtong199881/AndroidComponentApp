package com.mohanlv.base.utils

import android.util.Log

/**
 * 日志工具类
 */
object LogUtils {
    
    private const val TAG = "ComponentApp"
    var isDebug = true
    
    fun d(tag: String = TAG, msg: String) { if (isDebug) Log.d(tag, msg) }
    fun i(tag: String = TAG, msg: String) { if (isDebug) Log.i(tag, msg) }
    fun w(tag: String = TAG, msg: String) { Log.w(tag, msg) }
    fun e(tag: String = TAG, msg: String, t: Throwable? = null) { Log.e(tag, msg, t) }
    fun v(tag: String = TAG, msg: String) { if (isDebug) Log.v(tag, msg) }
}
