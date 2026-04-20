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
 */
object ReactNativeHelper {
    
    private const val TAG = "ReactNativeHelper"
    
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
    
    private fun createReactInstanceManager(application: Application): ReactInstanceManager {
        // 获取本地 bundle 文件
        val bundleFile = File(application.getDir("react-native", Application.MODE_PRIVATE), "index.android.bundle")
        
        // 选择 bundle 加载方式
        val jsBundleLoader = if (bundleFile.exists()) {
            Log.d(TAG, "Loading bundle from file: ${bundleFile.absolutePath}")
            JSBundleLoader.createFileLoader(bundleFile.absolutePath)
        } else {
            Log.d(TAG, "Loading bundle from assets")
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
