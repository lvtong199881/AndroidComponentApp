package com.mohanlv.homemodule.api

import com.mohanlv.basemodule.network.BaseRepository
import com.mohanlv.basemodule.network.HttpClient
import com.mohanlv.basemodule.network.Resource
import com.mohanlv.homemodule.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlinx.coroutines.withContext as kotlinWithContext

/**
 * Pexels 视频 API 服务
 */
class PexelsApiService : BaseRepository() {

    companion object {
        private const val BASE_URL = "https://api.pexels.com/videos"
        private const val API_KEY = "YOUR_PEXELS_API_KEY" // TODO: 替换为你的 Pexels API Key
    }

    /**
     * 获取热门视频列表
     */
    suspend fun getPopularVideos(page: Int, perPage: Int): Resource<List<Video>> {
        return safeApiCall {
            val url = "$BASE_URL/popular?page=$page&per_page=$perPage"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", API_KEY)
                .get()
                .build()

            val response = HttpClient.okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("API error: ${response.code}")
            }
            val body = response.body?.string() ?: ""
            parseVideoList(body)
        }
    }

    private fun parseVideoList(json: String?): List<Video> {
        val videos = mutableListOf<Video>()
        json ?: return videos

        val jsonObject = JSONObject(json)
        val videoArray = jsonObject.optJSONArray("videos") ?: return videos

        for (i in 0 until videoArray.length()) {
            val videoObj = videoArray.getJSONObject(i)
            val videoFiles = videoObj.optJSONArray("video_files") ?: continue
            val videoPictures = videoObj.optJSONArray("video_pictures") ?: continue

            // 获取最佳质量的视频URL
            var videoUrl = ""
            var maxHeight = 0
            for (j in 0 until videoFiles.length()) {
                val file = videoFiles.getJSONObject(j)
                val height = file.optInt("height", 0)
                if (height > maxHeight && height <= 1080) {
                    maxHeight = height
                    videoUrl = file.optString("link", "")
                }
            }

            if (videoUrl.isEmpty()) continue

            // 获取封面图
            val coverUrl = if (videoPictures.length() > 0) {
                videoPictures.getString(0)
            } else ""

            // 获取用户信息
            val userObj = videoObj.optJSONObject("user")
            val authorName = userObj?.optString("name", "Unknown") ?: "Unknown"

            // 获取时长
            val duration = videoObj.optLong("duration", 0)

            videos.add(
                Video(
                    id = videoObj.optLong("id", 0),
                    videoUrl = videoUrl,
                    coverUrl = coverUrl,
                    title = "@$authorName 的短视频",
                    authorName = authorName,
                    authorAvatar = userObj?.optString("url", "") ?: "",
                    likeCount = (Math.random() * 10000).toLong(),
                    commentCount = (Math.random() * 1000).toLong(),
                    shareCount = (Math.random() * 500).toLong(),
                    duration = duration
                )
            )
        }
        return videos
    }
}