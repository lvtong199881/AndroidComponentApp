package com.mohanlv.shortvideo.model

import com.google.gson.annotations.SerializedName

/**
 * 视频数据模型
 * 使用 Pexels Videos API: https://www.pexels.com/api/
 *
 * @property id 视频ID
 * @property width 视频宽度
 * @property height 视频高度
 * @property url 视频页面URL
 * @property image 封面图片URL
 * @property duration 视频时长（秒）
 * @property user 视频作者信息
 * @property videoFiles 视频文件列表
 * @property videoPictures 视频图片列表
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
    /**
     * 获取最佳质量的视频URL
     */
    fun getBestVideoUrl(): String? {
        return videoFiles
            ?.filter { it.link?.isNotEmpty() == true }
            ?.sortedByDescending { it.quality?.let { q -> q.removeSuffix("p").toIntOrNull() } ?: 0 }
            ?.firstOrNull()
            ?.link
    }

    /**
     * 获取视频时长（格式化）
     * 例如：90秒 -> "01:30"
     */
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

/**
 * 照片数据模型
 * 使用 Pexels Photos API
 *
 * @property id 照片ID
 * @property width 照片宽度
 * @property height 照片高度
 * @property url 照片页面URL
 * @property photographer 摄影师名称
 * @property photographerUrl 摄影师主页
 * @property src 图片资源URL
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