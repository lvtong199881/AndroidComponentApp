package com.mohanlv.base

import android.app.Application
import com.mohanlv.logger.Logger
import com.mohanlv.logger.LoggerConfig
import com.mohanlv.startup.StartupTask
import java.io.File

import com.mohanlv.startup.annotation.InitTask

/**
 * Logger 模块的初始化任务
 */
@InitTask(key = "logger", priority = 100)
class LoggerStartupTask(private val application: Application) : StartupTask {

    override val name: String = "LoggerStartupTask"

    override val priority: Int = 100  // 在 BaseStartupTask 之后执行

    override fun create() {
        LoggerConfig.logDir = File(
            application.filesDir,
            "logs"
        )
        Logger.init()
    }
}
