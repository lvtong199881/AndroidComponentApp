package com.mohanlv.home

import android.os.Bundle
import com.mohanlv.common.GsonUtils
import com.mohanlv.router.RouterManager
import com.mohanlv.home.model.MediaItem

/**
 * 跳转到图片/视频详情页
 */
fun navigateToDetail(media: MediaItem) {
    when (media.type) {
        "Photo" -> {
            val photoJson = GsonUtils.toJson(media)
            RouterManager.navigate(
                "oneandroid://shortvideo/detail",
                Bundle().apply { putString("extra_photo", photoJson) }
            )
        }
        "Video" -> {
            val videoJson = GsonUtils.toJson(media)
            RouterManager.navigate(
                "oneandroid://shortvideo/detail",
                Bundle().apply { putString("extra_video", videoJson) }
            )
        }
    }
}