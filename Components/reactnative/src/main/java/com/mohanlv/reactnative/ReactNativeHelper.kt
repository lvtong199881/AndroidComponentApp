package com.mohanlv.reactnative

import android.app.Application
import android.util.Log
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import java.io.File

/**
 * ReactInstanceManager 持有者，懒创建
 * 只负责初始化，实际 bundle 加载由 RNFragment 处理
 */
object ReactNativeHelper {
    
    private const val TAG = "RN"
    
    private var reactInstanceManager: ReactInstanceManager? = null
    private var isInitialized = false
    
    /**
     * 初始化 ReactInstanceManager
     */
    fun init(application: Application, reactNativeHost: ReactNativeHost) {
        if (isInitialized) {
            Log.w(TAG, "ReactNativeHelper already initialized")
            return
        }
        
        reactInstanceManager = createReactInstanceManager(application)
        isInitialized = true
        Log.d(TAG, "ReactNativeHelper initialized")
    }
    
    fun get(): ReactInstanceManager? = reactInstanceManager
    
    fun isInitialized(): Boolean = isInitialized
    
    /**
     * 创建 ReactInstanceManager（bundle 加载方式由调用方指定）
     */
    private fun createReactInstanceManager(application: Application): ReactInstanceManager {
        // 优先从本地文件加载，不存在则从 assets 加载
        val bundleFile = File(application.getDir("react-native", Application.MODE_PRIVATE), "index.android.bundle")
        val jsBundleLoader = if (bundleFile.exists()) {
            Log.d(TAG, "Bundle from file: ${bundleFile.absolutePath}")
            JSBundleLoader.createFileLoader(bundleFile.absolutePath)
        } else {
            Log.d(TAG, "Bundle from assets")
            JSBundleLoader.createAssetLoader(application, "index.android.bundle", false)
        }
        
        return ReactInstanceManager.builder()
            .setCurrentActivity(null)
            .setApplication(application)
            .setJSMainModulePath("index")
            .setJSBundleLoader(jsBundleLoader)
            .addPackage(MainReactPackage())
            .setUseDeveloperSupport(false)
            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
            .build()
    }
}
