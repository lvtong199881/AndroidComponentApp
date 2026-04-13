package com.mohanlv.network

import android.content.Context
import com.mohanlv.network.interceptor.HeaderInterceptor
import com.mohanlv.network.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络管理器
 */
object NetworkManager {
    
    private const val DEFAULT_BASE_URL = "https://api.example.com/"
    
    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private var isInitialized = false
    
    fun init(context: Context, baseUrl: String = DEFAULT_BASE_URL) {
        if (isInitialized) return
        
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor(context))
            .addInterceptor(LoggingInterceptor())
            .retryOnConnectionFailure(true)
            .build()
        
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        isInitialized = true
    }
    
    fun <T> createApi(serviceClass: Class<T>): T {
        check(isInitialized) { "NetworkManager not initialized" }
        return retrofit!!.create(serviceClass)
    }
    
    fun getOkHttpClient(): OkHttpClient {
        check(isInitialized) { "NetworkManager not initialized" }
        return okHttpClient!!
    }
}
