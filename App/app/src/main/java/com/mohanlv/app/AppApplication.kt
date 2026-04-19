package com.mohanlv.app

import android.app.Application
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import com.mohanlv.base.utils.AppUtils
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.network.NetworkManager
import com.mohanlv.router.RouterManager

class AppApplication : Application(), ReactApplication {
    
    override lateinit var reactNativeHost: ReactNativeHost
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 React Native SoLoader（必须在其他RN代码之前）
        SoLoader.init(this, OpenSourceMergedSoMapping)
        
        // 初始化 React Native Host
        reactNativeHost = object : DefaultReactNativeHost(this) {
            override fun getJSMainModuleName(): String = "index"
            
            override fun getUseDeveloperSupport(): Boolean = false
            
            override fun getPackages(): List<ReactPackage> {
                return listOf(com.facebook.react.shell.MainReactPackage())
            }
            
            override val isNewArchEnabled: Boolean = false
            override val isHermesEnabled: Boolean = true  // 使用 Hermes 引擎（支持 64 位）
        }
        
        // 初始化工具类
        AppUtils.init(this)
        SPUtils.init(this)
        
        // 初始化网络（使用 WanAndroid API）
        NetworkManager.init(this)
        
        // 初始化路由（自动扫描注册带 @Route 注解的 Fragment）
        RouterManager.init(this, R.id.container)
    }
}
