package com.mohanlv.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 网络请求日志拦截器
 * 自动记录所有请求和响应，默认 info 级别，错误用 error 级别
 */
class LoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "Network"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        
        Log.i(TAG, "[请求] $method $url")
        
        val startTime = System.nanoTime()
        
        return try {
            val response = chain.proceed(request)
            val duration = (System.nanoTime() - startTime) / 1_000_000
            
            Log.i(TAG, "[响应] ${response.code} ${duration}ms $url")
            
            response
        } catch (e: Exception) {
            val duration = (System.nanoTime() - startTime) / 1_000_000
            Log.e(TAG, "[错误] $method 失败 ${duration}ms $url")
            Log.e(TAG, "     错误: ${e.javaClass.simpleName}: ${e.message}")
            Log.e(TAG, "     堆栈: ${Log.getStackTraceString(e)}")
            throw e
        }
    }
}
