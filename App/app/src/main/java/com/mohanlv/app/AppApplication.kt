package com.mohanlv.app

import android.app.Application
import com.mohanlv.base.BaseStartupTask
import com.mohanlv.base.utils.GlobalAppContext
import com.mohanlv.base.LoggerStartupTask
import com.mohanlv.network.NetworkStartupTask
import com.mohanlv.reactnative.ReactNativeStartupTask
import com.mohanlv.router.RouterStartupTask
import com.mohanlv.login.LoginValidationTask
import com.mohanlv.startup.StartupManager

class AppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        GlobalAppContext.init(this)
        // 注册各模块的初始化任务（按 priority 排序执行）
        StartupManager.register(BaseStartupTask(this))
        StartupManager.register(LoggerStartupTask(this))
        StartupManager.register(NetworkStartupTask(this))
        StartupManager.register(RouterStartupTask(this, R.id.container))
        StartupManager.register(ReactNativeStartupTask(this))
        StartupManager.register(LoginValidationTask(this))
        
        // 启动所有初始化任务
        StartupManager.start()
    }
}
