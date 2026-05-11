package com.mohanlv.shortvideo.model

import com.google.gson.annotations.SerializedName

/**
 * 照片数据模型
 * 使用 Pexels Photos API
 */
data class Photo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("photographer")
    val photographer: String,

    @SerializedName("photographer_url")
    val photographerUrl: String,

    @SerializedName("photographer_id")
    val photographerId: Long,

    @SerializedName("avg_color")
    val avgColor: String?,

    @SerializedName("src")
    val src: PhotoSrc,

    @SerializedName("liked")
    val liked: Boolean,

    @SerializedName("alt")
    val alt: String
)

/**
 * 照片资源URL
 */
data class PhotoSrc(
    @SerializedName("original")
    val original: String,

    @SerializedName("large2x")
    val large2x: String,

    @SerializedName("large")
    val large: String,

    @SerializedName("medium")
    val medium: String,

    @SerializedName("small")
    val small: String,

    @SerializedName("portrait")
    val portrait: String,

    @SerializedName("landscape")
    val landscape: String,

    @SerializedName("tiny")
    val tiny: String
)

/**
 * Pexels API 响应模型（Photos）
 */
data class PexelsPhotoResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("photos")
    val photos: List<Photo>?,

    @SerializedName("next_page")
    val nextPage: String?
)