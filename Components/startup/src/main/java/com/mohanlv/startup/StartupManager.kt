package com.mohanlv.startup

/**
 * 模块初始化管理器
 * 负责收集和执行各模块的 StartupTask
 */
object StartupManager {
    
    private val tasks = mutableListOf<StartupTask>()
    private var isInitialized = false
    
    /**
     * 注册初始化任务
     */
    fun register(task: StartupTask) {
        if (isInitialized) {
            logW("StartupManager::已初始化，忽略注册任务: ${task.name}")
            return
        }
        tasks.add(task)
        logD("注册启动任务: ${task.name} (优先级: ${task.priority})")
    }
    
    /**
     * 执行所有已注册的任务
     */
    fun start() {
        if (isInitialized) {
            logW("StartupManager::已启动")
            return
        }
        
        // 按优先级排序
        tasks.sortBy { it.priority }
        
        logI("正在启动 ${tasks.size} 个任务...")
        
        tasks.forEach { task ->
            try {
                logD("执行任务: ${task.name}")
                task.create()
                logD("任务完成: ${task.name}")
            } catch (e: Exception) {
                logE("任务执行失败: ${task.name}", e)
            }
        }
        
        logI("所有任务执行完成")
        isInitialized = true
    }
}
