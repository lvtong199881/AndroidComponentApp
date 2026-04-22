package com.mohanlv.startup

import android.app.Application

/**
 * 启动任务收集器接口
 * 由 init-annotator KAPT 生成器实现，每个模块生成一个实现类
 */
interface StartupCollector {
    /**
     * 收集该模块的所有 StartupTask
     */
    fun collect(application: Application): List<StartupTask>
}
