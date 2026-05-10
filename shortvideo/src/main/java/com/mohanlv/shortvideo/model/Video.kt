package com.mohanlv.shortvideo.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
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

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        width = parcel.readInt(),
        height = parcel.readInt(),
        url = parcel.readString() ?: "",
        image = parcel.readString() ?: "",
        duration = parcel.readInt(),
        user = parcel.readParcelable(VideoUser::class.java.classLoader) ?: VideoUser(0, "", ""),
        videoFiles = parcel.createTypedArrayList(VideoFile) ?: emptyList(),
        videoPictures = parcel.createTypedArrayList(VideoPicture) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(url)
        parcel.writeString(image)
        parcel.writeInt(duration)
        parcel.writeParcelable(user, flags)
        parcel.writeTypedList(videoFiles)
        parcel.writeTypedList(videoPictures)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video = Video(parcel)
        override fun newArray(size: Int): Array<Video?> = arrayOfNulls(size)
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        name = parcel.readString() ?: "",
        url = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VideoUser> {
        override fun createFromParcel(parcel: Parcel): VideoUser = VideoUser(parcel)
        override fun newArray(size: Int): Array<VideoUser?> = arrayOfNulls(size)
    }
}

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        quality = parcel.readString(),
        fileType = parcel.readString(),
        width = parcel.readValue(Int::class.java.classLoader) as? Int,
        height = parcel.readValue(Int::class.java.classLoader) as? Int,
        link = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(quality)
        parcel.writeString(fileType)
        parcel.writeValue(width)
        parcel.writeValue(height)
        parcel.writeString(link)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VideoFile> {
        override fun createFromParcel(parcel: Parcel): VideoFile = VideoFile(parcel)
        override fun newArray(size: Int): Array<VideoFile?> = arrayOfNulls(size)
    }
}

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        nr = parcel.readValue(Int::class.java.classLoader) as? Int,
        picture = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeValue(nr)
        parcel.writeString(picture)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VideoPicture> {
        override fun createFromParcel(parcel: Parcel): VideoPicture = VideoPicture(parcel)
        override fun newArray(size: Int): Array<VideoPicture?> = arrayOfNulls(size)
    }
}

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