package com.mohanlv.home.model

import com.google.gson.annotations.SerializedName

/**
 * Pexels 合集数据模型
 * 文档：https://www.pexels.com/api/documentation/#collections-featured
 */
data class PexelsCollection(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("private")
    val isPrivate: Boolean?,

    @SerializedName("media_count")
    val mediaCount: Int,

    @SerializedName("photos_count")
    val photosCount: Int,

    @SerializedName("videos_count")
    val videosCount: Int
)

/**
 * Pexels Collections API 响应模型
 */
data class PexelsCollectionResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("collections")
    val collections: List<PexelsCollection>?,

    @SerializedName("next_page")
    val nextPage: String?
)