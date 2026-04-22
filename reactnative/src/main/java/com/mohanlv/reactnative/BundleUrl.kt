package com.mohanlv.reactnative

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * React Native Bundle URL 工具
 */
object BundleUrl {
    
    private const val OWNER = "lvtong199881"
    private const val REPO = "MyRNApp"
    
    enum class BuildType {
        DEBUG,
        RELEASE
    }
    
    private const val GITHUB_RELEASE_DOWNLOAD = "https://github.com/%s/%s/releases/download/%s/%s"
    private const val GITHUB_API_LATEST = "https://api.github.com/repos/%s/%s/releases/latest"
    
    /**
     * 获取指定 repo 和版本的 bundle 链接
     */
    fun getBundleUrl(
        repo: String,
        version: String,
        buildType: BuildType = BuildType.RELEASE
    ): String {
        val bundleName = "index.android.bundle"
        return GITHUB_RELEASE_DOWNLOAD.format(OWNER, repo, version, bundleName)
    }
    
    /**
     * 获取指定 repo 和版本的 debug bundle 链接
     */
    fun getDebugBundleUrl(repo: String, version: String): String {
        return getBundleUrl(repo, version, BuildType.DEBUG)
    }
    
    /**
     * 获取指定 repo 和版本的 release bundle 链接
     */
    fun getReleaseBundleUrl(repo: String, version: String): String {
        return getBundleUrl(repo, version, BuildType.RELEASE)
    }
    
    /**
     * 获取指定 repo 的最新版本号（tag_name）
     */
    suspend fun getLatestVersion(repo: String): String? = withContext(Dispatchers.IO) {
        try {
            val apiUrl = GITHUB_API_LATEST.format(OWNER, repo)
            log("BundleUrl::正在获取最新版本信息: $apiUrl")
            
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                connectTimeout = 10000
                readTimeout = 10000
            }
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val tagName = json.optString("tag_name", "")
                
                if (tagName.isNotEmpty()) {
                    log("BundleUrl::最新版本: $tagName")
                    return@withContext tagName
                }
            } else {
                logW("BundleUrl::获取最新版本失败: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            logE("BundleUrl::获取最新版本异常", e)
        }
        return@withContext null
    }
}
