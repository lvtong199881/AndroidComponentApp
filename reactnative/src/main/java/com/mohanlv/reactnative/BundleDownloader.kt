package com.mohanlv.reactnative

import android.content.Context
import com.mohanlv.reactnative.logE
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
    private val bundleFileName: String = "index.android.bundle",
    private val maxRetry: Int = DEFAULT_MAX_RETRY
) {
    
    companion object {
        // 默认远端 bundle URL（可通过配置修改）
        const val DEFAULT_BUNDLE_URL = "https://your-cdn.com/bundle/index.android.bundle"
        
        // 默认最大重试次数
        const val DEFAULT_MAX_RETRY = 3
        
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
            log("BundleDownloader::本地 bundle 已存在: ${localFile.absolutePath}")
            return@withContext localFile
        }
        
        // 本地不存在，下载
        log("BundleDownloader::本地 bundle 不存在，开始下载: $bundleUrl")
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
     * @return 下载成功的文件，失败返回 null
     */
    private fun download(): File? {
        var retry = 0
        var lastException: Exception? = null
        
        while (retry < maxRetry) {
            try {
                log("BundleDownloader::正在下载 bundle (第 ${retry + 1} 次/$maxRetry)...")
                
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
                        log("BundleDownloader::Bundle 下载成功: ${localFile.absolutePath}")
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
                logE("BundleDownloader::下载失败 (第 ${retry + 1} 次)", e)
                lastException = e
                retry++
                // 无需等待，立即重试
            }
        }
        
        logE("BundleDownloader::Bundle 下载失败，已重试 $maxRetry 次", lastException)
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
        
        // TODO: 比对版本或 hash
        return@withContext false
    }
}
