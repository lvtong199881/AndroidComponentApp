package com.mohanlv.reactnative

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.HttpRetryException
import java.net.URL

/**
 * React Native Bundle 下载器
 * 负责从远端下载 JS Bundle 到本地
 */
class BundleDownloader(
    private val context: Context,
    private val bundleUrl: String,
    private val bundleFileName: String = "index.android.bundle"
) {
    
    companion object {
        private const val TAG = "RN"
        
        // 默认远端 bundle URL（可通过配置修改）
        const val DEFAULT_BUNDLE_URL = "https://your-cdn.com/bundle/index.android.bundle"
        
        // 最大重试次数
        private const val MAX_RETRY = 3
        
        // 连接超时（毫秒）
        private const val CONNECT_TIMEOUT = 30000
        
        // 读取超时（毫秒）
        private const val READ_TIMEOUT = 60000
    }
    
    /**
     * 获取本地 bundle 文件
     * 如果本地不存在或需要更新，触发下载
     */
    suspend fun getLocalBundle(): File? = withContext(Dispatchers.IO) {
        val localFile = getLocalBundleFile()
        
        // 检查本地文件是否存在
        if (localFile.exists()) {
            Log.d(TAG, "本地 bundle 已存在: ${localFile.absolutePath}")
            return@withContext localFile
        }
        
        // 本地不存在，下载
        Log.d(TAG, "本地 bundle 不存在，开始下载: $bundleUrl")
        return@withContext download()
    }
    
    /**
     * 强制重新下载 bundle
     */
    suspend fun forceDownload(): File? = withContext(Dispatchers.IO) {
        download()
    }
    
    /**
     * 下载 bundle 到本地
     */
    private fun download(): File? {
        var retry = 0
        var lastException: Exception? = null
        
        while (retry < MAX_RETRY) {
            try {
                Log.d(TAG, "正在下载 bundle (第 ${retry + 1} 次/$MAX_RETRY)...")
                
                val url = URL(bundleUrl)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = CONNECT_TIMEOUT
                    readTimeout = READ_TIMEOUT
                    setRequestProperty("Accept", "application/javascript")
                }
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val localFile = getLocalBundleFile()
                    
                    // 确保目录存在
                    localFile.parentFile?.mkdirs()
                    
                    // 下载到临时文件，下载完成后再重命名
                    val tempFile = File(localFile.parent, "${bundleFileName}.tmp")
                    
                    connection.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    // 原子性重命名
                    if (tempFile.renameTo(localFile)) {
                        Log.i(TAG, "Bundle 下载成功: ${localFile.absolutePath}")
                        return localFile
                    } else {
                        throw IllegalStateException("Failed to rename temp file to bundle")
                    }
                } else {
                    throw HttpRetryException(
                        "HTTP ${connection.responseCode}: ${connection.responseMessage}",
                        connection.responseCode
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "下载失败", e)
                lastException = e
                retry++
                
                if (retry < MAX_RETRY) {
                    // 指数退避
                    val backoff = (1000L * (1 shl (retry - 1))).coerceAtMost(10000L)
                    Log.d(TAG, "${backoff}ms 后重试...")
                    Thread.sleep(backoff)
                }
            }
        }
        
        Log.e(TAG, "Bundle 下载失败，已重试 $MAX_RETRY 次", lastException)
        return null
    }
    
    /**
     * 获取本地 bundle 文件路径
     */
    private fun getLocalBundleFile(): File {
        return File(context.getDir("react-native", Context.MODE_PRIVATE), bundleFileName)
    }
    
    /**
     * 检查是否需要下载更新（通过比对版本或 hash）
     */
    suspend fun checkForUpdate(remoteVersion: String?): Boolean = withContext(Dispatchers.IO) {
        val localFile = getLocalBundleFile()
        if (!localFile.exists()) return@withContext true
        
        // TODO: 实现版本比对逻辑
        // 可以通过比对 bundle 的 hash 或版本号来判断是否需要更新
        return@withContext false
    }
}
