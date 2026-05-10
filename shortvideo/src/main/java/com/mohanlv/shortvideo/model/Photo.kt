package com.mohanlv.shortvideo.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        width = parcel.readInt(),
        height = parcel.readInt(),
        url = parcel.readString() ?: "",
        photographer = parcel.readString() ?: "",
        photographerUrl = parcel.readString() ?: "",
        photographerId = parcel.readLong(),
        avgColor = parcel.readString(),
        src = parcel.readParcelable(PhotoSrc::class.java.classLoader) ?: PhotoSrc("", "", "", "", "", "", "", ""),
        liked = parcel.readByte() != 0.toByte(),
        alt = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(url)
        parcel.writeString(photographer)
        parcel.writeString(photographerUrl)
        parcel.writeLong(photographerId)
        parcel.writeString(avgColor)
        parcel.writeParcelable(src, flags)
        parcel.writeByte(if (liked) 1 else 0)
        parcel.writeString(alt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo = Photo(parcel)
        override fun newArray(size: Int): Array<Photo?> = arrayOfNulls(size)
    }
}

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        original = parcel.readString() ?: "",
        large2x = parcel.readString() ?: "",
        large = parcel.readString() ?: "",
        medium = parcel.readString() ?: "",
        small = parcel.readString() ?: "",
        portrait = parcel.readString() ?: "",
        landscape = parcel.readString() ?: "",
        tiny = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(original)
        parcel.writeString(large2x)
        parcel.writeString(large)
        parcel.writeString(medium)
        parcel.writeString(small)
        parcel.writeString(portrait)
        parcel.writeString(landscape)
        parcel.writeString(tiny)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PhotoSrc> {
        override fun createFromParcel(parcel: Parcel): PhotoSrc = PhotoSrc(parcel)
        override fun newArray(size: Int): Array<PhotoSrc?> = arrayOfNulls(size)
    }
}

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