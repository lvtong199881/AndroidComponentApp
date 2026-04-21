package com.mohanlv.startup

import android.util.Log

/**
 * 模块初始化管理器
 * 负责收集和执行各模块的 StartupTask
 */
object StartupManager {
    
    private const val TAG = "StartupManager"
    
    private val tasks = mutableListOf<StartupTask>()
    private var isInitialized = false
    
    /**
     * 注册初始化任务
     */
    fun register(task: StartupTask) {
        if (isInitialized) {
            Log.w(TAG, "StartupManager 已初始化，忽略注册任务: ${task.name}")
            return
        }
        tasks.add(task)
        Log.d(TAG, "注册启动任务: ${task.name} (优先级: ${task.priority})")
    }
    
    /**
     * 执行所有已注册的任务
     */
    fun start() {
        if (isInitialized) {
            Log.w(TAG, "StartupManager 已启动")
            return
        }
        
        // 按优先级排序
        tasks.sortBy { it.priority }
        
        Log.i(TAG, "正在启动 ${tasks.size} 个任务...")
        
        tasks.forEach { task ->
            try {
                Log.d(TAG, "执行任务: ${task.name}")
                task.create()
                Log.d(TAG, "任务完成: ${task.name}")
            } catch (e: Exception) {
                Log.e(TAG, "任务执行失败: ${task.name}", e)
            }
        }
        
        Log.i(TAG, "所有任务执行完成")
        isInitialized = true
    }
}
