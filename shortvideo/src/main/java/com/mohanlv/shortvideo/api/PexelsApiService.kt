package com.mohanlv.shortvideo.api

import com.mohanlv.shortvideo.model.PexelsResponse
import com.mohanlv.shortvideo.model.Video
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Pexels Videos API 服务接口
 * 文档：https://www.pexels.com/api/documentation/
 *
 * 注意：需要申请 API Key，免费账号每月 200 请求
 * 申请地址：https://www.pexels.com/api/
 *
 * API Key 占位符，后续替换为真实 Key
 */
interface PexelsApiService {

    companion object {
        const val API_KEY = "3WrPNOrJdLJ7w4aXad76Btqk0vQNvonSs3UqWSjRhv9FR6hixcdjWCsY"

        // API 基础地址
        const val BASE_URL = "https://api.pexels.com/"
    }

    /**
     * 获取热门视频
     * GET /videos/popular
     *
     * @param page 页码（默认1）
     * @param perPage 每页数量（默认15，最大100）
     */
    @GET("videos/popular")
    suspend fun getPopularVideos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<PexelsResponse>

    /**
     * 搜索视频
     * GET /videos/search
     *
     * @param query 搜索关键词
     * @param page 页码（默认1）
     * @param perPage 每页数量（默认15，最大100）
     */
    @GET("videos/search")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<PexelsResponse>
}

/**
 * Pexels API 客户端
 * 使用单独的 Retrofit 实例，不依赖全局 NetworkManager
 */
object PexelsApiClient {

    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private val okHttpClient: okhttp3.OkHttpClient by lazy {
        okhttp3.OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
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