package com.mohanlv.network.interceptor

import android.content.Context
import android.webkit.CookieManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 请求头拦截器
 * - 自动同步 WebView Cookie 到 OkHttp
 * - 添加常用请求头
 */
class HeaderInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 构建新的请求Builder
        val newRequestBuilder = originalRequest.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("Content-Type", "application/json; charset=UTF-8")
            .header("Accept", "application/json")
            .header("Referer", "https://www.wanandroid.com/")

        // 同步 WebView Cookie（用于保持登录状态）
        try {
            val cookieManager = CookieManager.getInstance()
            val url = originalRequest.url.toString()
            val webViewCookies = cookieManager.getCookie(url)
            if (!webViewCookies.isNullOrEmpty()) {
                newRequestBuilder.header("Cookie", webViewCookies)
            }
        } catch (e: Exception) {
            // 忽略 Cookie 同步错误
        }

        return chain.proceed(newRequestBuilder.build())
    }
}
