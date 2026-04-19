package com.mohanlv.app

import android.app.Application
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.base.BaseStartupTask
import com.mohanlv.network.NetworkStartupTask
import com.mohanlv.reactnative.ReactNativeStartupTask
import com.mohanlv.router.RouterStartupTask
import com.mohanlv.startup.StartupManager

class AppApplication : Application(), ReactApplication {
    
    override lateinit var reactNativeHost: ReactNativeHost
    
    override fun onCreate() {
        super.onCreate()
        
        // 注册各模块的初始化任务
        StartupManager.register(BaseStartupTask(this))
        StartupManager.register(NetworkStartupTask(this))
        StartupManager.register(RouterStartupTask(this, R.id.container))
        StartupManager.register(ReactNativeStartupTask(this))
        
        // 初始化 React Native Host（必须在 StartupManager.start() 之前）
        reactNativeHost = object : DefaultReactNativeHost(this) {
            override fun getJSMainModuleName(): String = "index"
            
            override fun getUseDeveloperSupport(): Boolean = false
            
            override fun getPackages(): List<ReactPackage> {
                return listOf(MainReactPackage())
            }
            
            override val isNewArchEnabled: Boolean = false
            override val isHermesEnabled: Boolean = true
        }
        
        // 启动所有初始化任务
        StartupManager.start()
    }
}
