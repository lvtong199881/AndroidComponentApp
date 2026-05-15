package com.mohanlv.home.model

import com.google.gson.annotations.SerializedName

/**
 * 合集媒体项（照片或视频）
 */
sealed class CollectionMedia {
    abstract val id: Long
    abstract val width: Int
    abstract val height: Int
    abstract val duration: Int?
}

/**
 * 合集中的照片
 */
data class CollectionPhoto(
    @SerializedName("id")
    override val id: Long,

    @SerializedName("width")
    override val width: Int,

    @SerializedName("height")
    override val height: Int,

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
) : CollectionMedia() {
    override val duration: Int? = null
}

/**
 * 合集中的视频
 */
data class CollectionVideo(
    @SerializedName("id")
    override val id: Long,

    @SerializedName("width")
    override val width: Int,

    @SerializedName("height")
    override val height: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("duration")
    override val duration: Int?,

    @SerializedName("user")
    val user: VideoUser,

    @SerializedName("video_files")
    val videoFiles: List<VideoFile>,

    @SerializedName("video_pictures")
    val videoPictures: List<VideoPicture>
) : CollectionMedia()

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
 * 视频作者信息
 */
data class VideoUser(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
)

/**
 * 视频文件信息
 */
data class VideoFile(
    @SerializedName("id")
    val id: Long,

    @SerializedName("quality")
    val quality: String?,

    @SerializedName("file_type")
    val fileType: String?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("link")
    val link: String?
)

/**
 * 视频图片信息
 */
data class VideoPicture(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nr")
    val nr: Int?,

    @SerializedName("picture")
    val picture: String?
)

/**
 * 合集媒体响应
 * type: all | photos | videos
 */
data class CollectionMediaResponse(
    @SerializedName("id")
    val id: String?,

    @SerializedName("page")
    val page: Int?,

    @SerializedName("per_page")
    val perPage: Int?,

    @SerializedName("total_results")
    val totalResults: Int?,

    @SerializedName("collections")
    val collections: List<PexelsCollection>?,

    @SerializedName("media")
    val media: List<MediaItem>?,

    @SerializedName("next_page")
    val nextPage: String?
)

/**
 * 媒体项（统一 photos 和 videos）
 */
data class MediaItem(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("duration")
    val duration: Int?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("photographer")
    val photographer: String?,

    @SerializedName("photographer_url")
    val photographerUrl: String?,

    @SerializedName("photographer_id")
    val photographerId: Long?,

    @SerializedName("avg_color")
    val avgColor: String?,

    @SerializedName("src")
    val src: PhotoSrc?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("video_files")
    val videoFiles: List<VideoFile>?,

    @SerializedName("video_pictures")
    val videoPictures: List<VideoPicture>?,

    @SerializedName("user")
    val user: VideoUser?,

    @SerializedName("liked")
    val liked: Boolean?,

    @SerializedName("alt")
    val alt: String?
)