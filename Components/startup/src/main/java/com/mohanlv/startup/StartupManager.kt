package com.mohanlv.startup

import android.app.Application

/**
 * 模块初始化管理器
 * 负责加载 StartupTable 并执行各模块的 StartupTask
 */
object StartupManager {
    
    private var isInitialized = false
    
    /**
     * 执行所有已注册的任务
     * @param application Application 实例
     * @param extraParams 额外参数，如 containerId 等
     */
    fun start(application: Application, extraParams: Map<String, Any> = emptyMap()) {
        if (isInitialized) {
            logW("StartupManager::已启动")
            return
        }
        
        // 收集所有 task 实例
        val allTasks = StartupTable.collectTasks(application, extraParams).sortedBy { it.priority }
        
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
