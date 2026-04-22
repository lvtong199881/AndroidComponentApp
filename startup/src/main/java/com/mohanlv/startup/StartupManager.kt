package com.mohanlv.startup

import android.app.Application
import java.util.ServiceLoader

/**
 * 模块初始化管理器
 * 运行时通过 ServiceLoader 发现所有 StartupCollector 实现，收集所有 StartupTask 并按 priority 排序执行
 */
object StartupManager {
    
    private var isInitialized = false
    
    fun start(application: Application) {
        if (isInitialized) {
            logW("StartupManager::已启动")
            return
        }
        
        // 通过 ServiceLoader 加载所有 StartupCollector
        val allTasks = ServiceLoader.load(StartupCollector::class.java)
            .flatMap { it.collect(application) }
            .sortedBy { it.priority }
        
        log("正在启动 ${allTasks.size} 个任务...")
        
        allTasks.forEachIndexed { index, task ->
            try {
                log("[$index/${allTasks.size}] 执行任务: ${task.name}")
                task.create()
                log("[$index/${allTasks.size}] 任务完成: ${task.name}")
            } catch (e: Exception) {
                logE("任务执行失败: ${task.name}", e)
            }
        }
        
        log("所有任务执行完成")
        isInitialized = true
    }
}
