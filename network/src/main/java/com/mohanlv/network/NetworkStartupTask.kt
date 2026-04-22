package com.mohanlv.network

import android.app.Application
import com.mohanlv.startup.StartupTask

import com.mohanlv.startup.annotation.InitTask

/**
 * Network 模块的初始化任务
 */
@InitTask(key = "network", priority = 200)
class NetworkStartupTask(private val application: Application) : StartupTask {
    
    override val name: String = "NetworkStartupTask"
    
    override val priority: Int = 100  // 在 Base 之后执行
    
    override fun create() {
        NetworkManager.init(application)
    }
}
