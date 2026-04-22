package com.mohanlv.app

import android.app.Application
import com.mohanlv.base.utils.GlobalAppContext
import com.mohanlv.startup.StartupManager

class AppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        GlobalAppContext.init(this)
        
        // 启动所有初始化任务（通过 @InitTask 注解自动注册）
        StartupManager.start(this)
    }
}
