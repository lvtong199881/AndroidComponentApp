package com.mohanlv.homemodule.model

import java.io.Serializable

/**
 * 视频数据模型
 */
data class Video(
    val id: Long,
    val videoUrl: String,
    val coverUrl: String,
    val title: String,
    val authorName: String,
    val authorAvatar: String,
    val likeCount: Long,
    val commentCount: Long,
    val shareCount: Long,
    val duration: Long
) : Serializable