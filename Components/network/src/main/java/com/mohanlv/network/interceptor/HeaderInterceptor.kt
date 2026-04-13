package com.mohanlv.network.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 请求头拦截器
 * - 添加常用请求头
 */
class HeaderInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequestBuilder = originalRequest.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("Content-Type", "application/json; charset=UTF-8")
            .header("Accept", "application/json")
            .header("Referer", "https://www.wanandroid.com/")

        return chain.proceed(newRequestBuilder.build())
    }
}
