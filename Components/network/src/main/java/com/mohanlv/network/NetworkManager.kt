package com.mohanlv.network

import android.content.Context
import com.mohanlv.network.api.ApiService
import com.mohanlv.network.interceptor.HeaderInterceptor
import com.mohanlv.network.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络管理器（单例）
 * 使用 WanAndroid 开放 API: https://www.wanandroid.com/
 */
object NetworkManager {

    // WanAndroid API 基础地址
    private const val BASE_URL = "https://wanandroid.com/"

    // 超时配置
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private var isInitialized = false

    /**
     * 初始化网络管理器
     * @param context Application Context
     */
    fun init(context: Context) {
        if (isInitialized) return

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor(context))
            .addInterceptor(LoggingInterceptor())
            .retryOnConnectionFailure(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        isInitialized = true
    }

    /**
     * 创建 API 服务
     * @param serviceClass API 接口 Class
     */
    fun <T> createApi(serviceClass: Class<T>): T {
        check(isInitialized) { "NetworkManager not initialized, please call init(context) first" }
        return retrofit!!.create(serviceClass)
    }

    /**
     * 获取 OkHttpClient 实例
     */
    fun getOkHttpClient(): OkHttpClient {
        check(isInitialized) { "NetworkManager not initialized" }
        return okHttpClient!!
    }
}
