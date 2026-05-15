package com.mohanlv.home.api

import com.mohanlv.home.model.CollectionMediaResponse
import com.mohanlv.home.model.PexelsCollectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Pexels API 服务接口（Collections）
 * 文档：https://www.pexels.com/api/documentation/
 */
interface PexelsApiService {

    companion object {
        const val API_KEY = "3WrPNOrJdLJ7w4aXad76Btqk0vQNvonSs3UqWSjRhv9FR6hixcdjWCsY"
        const val BASE_URL = "https://api.pexels.com/"
    }

    /**
     * 获取精选合集
     * GET /v1/collections/featured
     *
     * @param page 页码（默认1）
     * @param perPage 每页数量（默认15，最大100）
     */
    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<PexelsCollectionResponse>

    /**
     * 获取合集媒体
     * GET /v1/collections/{id}
     *
     * @param id 合集ID
     * @param page 页码（默认1）
     * @param perPage 每页数量（默认15，最大100）
     * @param type 媒体类型：all | photos | videos
     */
    @GET("v1/collections/{id}")
    suspend fun getCollectionMedia(
        @retrofit2.http.Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15,
        @Query("type") type: String = "all"
    ): Response<CollectionMediaResponse>
}