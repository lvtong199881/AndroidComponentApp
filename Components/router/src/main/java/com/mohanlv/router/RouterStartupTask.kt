package com.mohanlv.router

import android.app.Application
import com.mohanlv.startup.StartupTask

/**
 * Router 模块的初始化任务
 */
class RouterStartupTask(
    private val application: Application,
    private val containerId: Int
) : StartupTask {
    
    override val name: String = "RouterStartupTask"
    
    override val priority: Int = 200  // 在 Network 之后执行
    
    override fun create() {
        RouterManager.init(application, containerId)
    }
}
