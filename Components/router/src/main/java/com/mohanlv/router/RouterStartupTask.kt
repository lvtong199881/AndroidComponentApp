package com.mohanlv.router

import android.app.Application
import com.mohanlv.startup.StartupTask

import com.mohanlv.startup.annotation.InitTask

/**
 * Router 模块的初始化任务
 */
@InitTask(key = "router", priority = 300)
class RouterStartupTask(private val application: Application) : StartupTask {
    
    override val name: String = "RouterStartupTask"
    
    override val priority: Int = 300
    
    override fun create() {
        RouterManager.init(application)
    }
}
