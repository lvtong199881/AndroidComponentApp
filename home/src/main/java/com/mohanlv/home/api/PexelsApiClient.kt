package com.mohanlv.home.api

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Pexels API 客户端
 */
object PexelsApiClient {

    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", PexelsApiService.API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit: retrofit2.Retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(PexelsApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    val apiService: PexelsApiService by lazy {
        retrofit.create(PexelsApiService::class.java)
    }
}