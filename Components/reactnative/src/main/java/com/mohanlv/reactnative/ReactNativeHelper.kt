package com.mohanlv.reactnative

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * React Native 辅助类
 * StartupTask 只负责初始化 SoLoader 和保存 ReactNativeHost 引用
 * Bundle 的实际加载由 RNFragment 懒加载决定
 */
object ReactNativeHelper {
    
    private const val TAG = "RN"
    
    private var application: Application? = null
    private var reactNativeHost: ReactNativeHost? = null
    private var initialized = false
    
    /**
     * 初始化（由 StartupTask 调用）
     */
    fun init(application: Application, reactNativeHost: ReactNativeHost) {
        if (initialized) {
            Log.w(TAG, "ReactNativeHelper already initialized")
            return
        }
        this.application = application
        this.reactNativeHost = reactNativeHost
        this.initialized = true
        Log.d(TAG, "ReactNativeHelper initialized")
    }
    
    fun isInitialized(): Boolean = initialized
    
    fun getApplication(): Application? = application
    
    fun getReactNativeHost(): ReactNativeHost? = reactNativeHost
    
    /**
     * 根据配置解析并加载 bundle，返回用于创建 ReactInstanceManager 的 JSBundleLoader
     */
    fun resolveBundleLoader(config: BundleConfig): JSBundleLoader {
        val app = application ?: throw IllegalStateException("ReactNativeHelper not initialized")
        
        val bundlePath = resolveBundlePath(app, config)
        
        return if (bundlePath.isNotEmpty() && File(bundlePath).exists()) {
            Log.d(TAG, "Loading bundle from file: $bundlePath")
            JSBundleLoader.createFileLoader(bundlePath)
        } else {
            Log.d(TAG, "Loading bundle from assets (fallback)")
            JSBundleLoader.createAssetLoader(app, "index.android.bundle", false)
        }
    }
    
    /**
     * 根据配置解析 bundle 路径
     */
    private fun resolveBundlePath(app: Application, config: BundleConfig): String {
        val bundleDir = getBundleDir(app)
        
        if (config.version != null) {
            // 指定版本：检查本地是否存在
            val localPath = getLocalBundlePath(app, config.version)
            if (localPath != null) {
                Log.d(TAG, "Using local bundle for version: ${config.version}")
                return localPath
            }
            
            // 本地不存在，下载
            Log.d(TAG, "Downloading bundle version: ${config.version}")
            return downloadBundle(app, config)
        }
        
        // 未指定版本：自动检测
        val latestVersion = runBlocking { BundleUrl.getLatestVersion(config.repo) }
        if (latestVersion == null) {
            Log.w(TAG, "Failed to get latest version, trying local bundle")
            return getDefaultLocalBundlePath(app)
        }
        
        val localPath = getLocalBundlePath(app, latestVersion)
        if (localPath != null) {
            Log.d(TAG, "Local version: $latestVersion, latest: $latestVersion")
            return localPath
        }
        
        // 需要下载新版本
        Log.d(TAG, "Downloading latest bundle version: $latestVersion")
        return downloadBundle(app, config.copy(version = latestVersion))
    }
    
    /**
     * 获取本地特定版本的 bundle 文件路径
     */
    private fun getLocalBundlePath(app: Application, version: String): String? {
        val bundleFile = File(getBundleDir(app), "$version/index.android.bundle")
        return if (bundleFile.exists()) bundleFile.absolutePath else null
    }
    
    /**
     * 获取默认的本地 bundle（最新版本）
     */
    private fun getDefaultLocalBundlePath(app: Application): String {
        val bundleDir = getBundleDir(app)
        if (!bundleDir.exists()) return ""
        
        val versions = bundleDir.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith("v") }
            ?.sortedWith { a, b -> compareVersion(b.name, a.name) }
            ?: return ""
        
        if (versions.isNotEmpty()) {
            val bundleFile = File(versions.first(), "index.android.bundle")
            if (bundleFile.exists()) {
                Log.d(TAG, "Using default local bundle: ${versions.first().name}")
                return bundleFile.absolutePath
            }
        }
        return ""
    }
    
    /**
     * 下载 bundle 并保存到本地
     */
    private fun downloadBundle(app: Application, config: BundleConfig): String {
        val version = config.version ?: return ""
        val bundleUrl = BundleUrl.getBundleUrl(config.repo, version, config.buildType)
        
        val downloader = BundleDownloader(app, bundleUrl, "index.android.bundle")
        val file = runBlocking { downloader.getLocalBundle() }
        
        if (file != null) {
            // 移动到版本目录
            val versionDir = File(getBundleDir(app), version)
            versionDir.mkdirs()
            val destFile = File(versionDir, "index.android.bundle")
            file.copyTo(destFile, overwrite = true)
            file.delete()
            Log.d(TAG, "Bundle saved to: ${destFile.absolutePath}")
            return destFile.absolutePath
        }
        
        throw IllegalStateException("Failed to download bundle from: $bundleUrl")
    }
    
    private fun getBundleDir(app: Application): File {
        return File(app.getDir("react-native", Context.MODE_PRIVATE), "bundles")
    }
    
    /**
     * 比较版本号
     */
    private fun compareVersion(v1: String, v2: String): Int {
        val parts1 = v1.trimStart('v').split(".")
        val parts2 = v2.trimStart('v').split(".")
        
        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val p1 = parts1.getOrElse(i) { "0" }.toIntOrNull() ?: 0
            val p2 = parts2.getOrElse(i) { "0" }.toIntOrNull() ?: 0
            if (p1 != p2) return p1 - p2
        }
        return 0
    }
}
