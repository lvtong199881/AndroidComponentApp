package com.mohanlv.reactnative

import android.app.Application
import com.facebook.react.ReactApplication
import com.facebook.react.soloader.OpenSourceMergedSoMapping
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
        
        // 预创建 ReactInstanceManager
        ReactNativeHelper.init(application)
    }
}
