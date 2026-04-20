package com.mohanlv.reactnative

import android.util.Log
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
    
    enum class BuildType {
        DEBUG,
        RELEASE
    }
    
    private const val JS_DELIVR_CDN = "https://cdn.jsdelivr.net/gh/%s/%s@%s/%s"
    
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
        val path = if (buildType == BuildType.DEBUG) "assets/debug/$bundleName" else "assets/$bundleName"
        return JS_DELIVR_CDN.format(OWNER, repo, version, path)
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
            Log.d(TAG, "Fetching latest version: $apiUrl")
            
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
                    Log.d(TAG, "Latest version: $tagName")
                    return@withContext tagName
                }
            } else {
                Log.w(TAG, "Failed to fetch latest version: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching latest version", e)
        }
        return@withContext null
    }
    
    private const val TAG = "RN"
}
