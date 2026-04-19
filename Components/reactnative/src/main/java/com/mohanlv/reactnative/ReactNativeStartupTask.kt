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
 */
class ReactNativeStartupTask(private val application: Application) : StartupTask {
    
    override val name: String = "ReactNativeStartupTask"
    
    override val priority: Int = 300  // 在 Router 之后执行
    
    override fun create() {
        // 初始化 SoLoader
        SoLoader.init(application, OpenSourceMergedSoMapping)
        
        // 创建 ReactNativeHost
        val reactNativeHost = object : DefaultReactNativeHost(application) {
            override fun getJSMainModuleName(): String = "index"
            
            override fun getUseDeveloperSupport(): Boolean = false
            
            override fun getPackages(): List<ReactPackage> {
                return listOf(MainReactPackage())
            }
            
            override val isNewArchEnabled: Boolean = false
            override val isHermesEnabled: Boolean = true
        }
        
        // 预创建 ReactInstanceManager
        ReactNativeHelper.init(application, reactNativeHost)
    }
}
