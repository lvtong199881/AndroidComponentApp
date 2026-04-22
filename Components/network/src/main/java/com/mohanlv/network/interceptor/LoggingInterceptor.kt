package com.mohanlv.network.interceptor


import com.mohanlv.network.log
import com.mohanlv.network.logE
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 网络请求日志拦截器
 * 自动记录所有请求和响应，默认 info 级别，错误用 error 级别
 */
class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        
        log("LoggingInterceptor::[请求] $method $url")
        
        val startTime = System.nanoTime()
        
        return try {
            val response = chain.proceed(request)
            val duration = (System.nanoTime() - startTime) / 1_000_000
            
            log("LoggingInterceptor::[响应] ${response.code} ${duration}ms $url")
            
            response
        } catch (e: Exception) {
            val duration = (System.nanoTime() - startTime) / 1_000_000
            logE("LoggingInterceptor::[错误] $method 失败 ${duration}ms $url", e)
            throw e
        }
    }
}
