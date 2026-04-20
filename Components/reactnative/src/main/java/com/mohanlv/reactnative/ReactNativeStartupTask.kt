package com.mohanlv.reactnative

import android.app.Application
import android.util.Log
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.mohanlv.startup.StartupTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * React Native 模块的初始化任务
 */
class ReactNativeStartupTask(
    private val application: Application,
    private val bundleUrl: String = BundleDownloader.DEFAULT_BUNDLE_URL
) : StartupTask {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override val name: String = "ReactNativeStartupTask"
    
    override val priority: Int = 300  // 在 Router 之后执行
    
    override fun create() {
        // 初始化 SoLoader
        SoLoader.init(application, OpenSourceMergedSoMapping)
        
        // 异步下载 bundle（不阻塞主线程）
        scope.launch {
            try {
                val downloader = BundleDownloader(application, bundleUrl)
                val bundleFile = downloader.getLocalBundle()
                
                if (bundleFile != null) {
                    Log.d(TAG, "Bundle ready: ${bundleFile.absolutePath}")
                    // 创建 ReactNativeHost（此时 bundle 已就绪）
                    val reactNativeHost = createReactNativeHost(bundleFile.absolutePath)
                    ReactNativeHelper.init(application, reactNativeHost)
                } else {
                    Log.e(TAG, "Bundle download failed, using bundled asset as fallback")
                    // 下载失败时，使用打包在 APK 里的 bundle
                    val reactNativeHost = createReactNativeHost(null)
                    ReactNativeHelper.init(application, reactNativeHost)
                }
            } catch (e: Exception) {
                Log.e(TAG, "ReactNative initialization failed", e)
            }
        }
    }
    
    private fun createReactNativeHost(bundlePath: String?): ReactNativeHost {
        return object : DefaultReactNativeHost(application) {
            override fun getJSMainModuleName(): String = "index"
            
            override fun getUseDeveloperSupport(): Boolean = false
            
            override fun getPackages(): List<ReactPackage> {
                return listOf(MainReactPackage())
            }
            
            override val isNewArchEnabled: Boolean = false
            override val isHermesEnabled: Boolean = true
        }
    }
    
    companion object {
        private const val TAG = "ReactNativeStartup"
    }
}
