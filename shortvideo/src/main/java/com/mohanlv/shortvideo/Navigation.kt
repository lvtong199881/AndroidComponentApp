package com.mohanlv.shortvideo

import android.os.Bundle
import com.mohanlv.router.RouterManager
import com.mohanlv.shortvideo.model.Photo
import com.mohanlv.shortvideo.model.Video

/**
 * 跳转图片/视频详情页
 */
fun navigateToDetail(photo: Photo? = null, video: Video? = null) {
    RouterManager.navigate(
        "oneandroid://shortvideo/detail",
        Bundle().apply {
            photo?.let { putParcelable("extra_photo", it) }
            video?.let { putParcelable("extra_video", it) }
        }
    )
}