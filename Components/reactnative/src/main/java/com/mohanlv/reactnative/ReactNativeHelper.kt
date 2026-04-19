package com.mohanlv.reactnative

import android.app.Application
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage

/**
 * ReactInstanceManager 持有者，懒创建
 */
object ReactNativeHelper {
    
    private var reactInstanceManager: ReactInstanceManager? = null
    
    /**
     * 初始化 ReactInstanceManager
     */
    fun init(application: Application, reactNativeHost: ReactNativeHost) {
        if (reactInstanceManager == null) {
            reactInstanceManager = createReactInstanceManager(application)
        }
    }
    
    fun get(): ReactInstanceManager? = reactInstanceManager
    
    private fun createReactInstanceManager(application: Application): ReactInstanceManager {
        // 创建 JSBundleLoader
        val jsBundleLoader = JSBundleLoader.createAssetLoader(
            application,
            "index.android.bundle",
            false
        )
        
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
