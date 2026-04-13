package com.mohanlv.network.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken()
        val newRequest = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .apply { if (!token.isNullOrEmpty()) addHeader("Authorization", "Bearer $token") }
            .addHeader("X-Platform", "Android")
            .build()
        return chain.proceed(newRequest)
    }
    
    private fun getToken(): String? = null // TODO: 从 SharedPreferences 获取
}
