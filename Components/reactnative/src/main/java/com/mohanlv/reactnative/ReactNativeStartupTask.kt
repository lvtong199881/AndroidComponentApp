package com.mohanlv.reactnative

import android.app.Application
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.mohanlv.startup.StartupTask

/**
 * React Native 模块的初始化任务
 * 只负责初始化 SoLoader 和预创建 ReactNativeHost
 * 实际 bundle 加载由 RNFragment 自行处理
 */
class ReactNativeStartupTask(private val application: Application) : StartupTask {
    
    override val name: String = "ReactNativeStartupTask"
    
    override val priority: Int = 300  // 在 Router 之后执行
    
    override fun create() {
        // 初始化 SoLoader
        SoLoader.init(application, OpenSourceMergedSoMapping)
        
        // 创建 ReactNativeHost（实际加载 bundle 在 RNFragment 中进行）
        val reactNativeHost = createReactNativeHost()
        ReactNativeHelper.init(application, reactNativeHost)
    }
    
    private fun createReactNativeHost(): ReactNativeHost {
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
}
