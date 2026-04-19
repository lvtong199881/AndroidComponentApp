package com.mohanlv.base

import android.app.Application
import com.mohanlv.startup.StartupTask
import com.mohanlv.base.utils.AppUtils
import com.mohanlv.base.utils.SPUtils

/**
 * Base 模块的初始化任务
 */
class BaseStartupTask(private val application: Application) : StartupTask {
    
    override val name: String = "BaseStartupTask"
    
    override val priority: Int = 0  // 最先执行
    
    override fun create() {
        AppUtils.init(application)
        SPUtils.init(application)
    }
}
