package com.mohanlv.base

import android.app.Application
import com.mohanlv.startup.StartupTask
import com.mohanlv.startup.annotation.InitTask
import com.mohanlv.base.utils.AppUtils

/**
 * Base 模块的初始化任务
 */
@InitTask(key = "base", priority = 0)
class BaseStartupTask(private val application: Application) : StartupTask {

    override val name: String = "BaseStartupTask"

    override val priority: Int = 0  // 最先执行

    override fun create() {
        AppUtils.init(application)
    }
}
