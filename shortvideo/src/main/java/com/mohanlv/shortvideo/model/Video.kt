package com.mohanlv.shortvideo.model

import com.google.gson.annotations.SerializedName

/**
 * 视频数据模型
 * 使用 Pexels Videos API
 */
data class Video(
    @SerializedName("id")
    val id: Long,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("duration")
    val duration: Int,

    @SerializedName("user")
    val user: VideoUser,

    @SerializedName("video_files")
    val videoFiles: List<VideoFile>,

    @SerializedName("video_pictures")
    val videoPictures: List<VideoPicture>
) {
    fun getBestVideoUrl(): String? {
        return videoFiles
            .filter { it.link?.isNotEmpty() == true }
            .sortedByDescending { it.quality?.let { q -> q.removeSuffix("p").toIntOrNull() } ?: 0 }
            .firstOrNull()
            ?.link
    }

    fun getFormattedDuration(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

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
 * Pexels API 响应模型（Videos）
 */
data class PexelsResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("videos")
    val videos: List<Video>?,

    @SerializedName("next_page")
    val nextPage: String?
)