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
            Log.w(TAG, "StartupManager already initialized, ignoring register of ${task.name}")
            return
        }
        tasks.add(task)
        Log.d(TAG, "Registered startup task: ${task.name} (priority: ${task.priority})")
    }
    
    /**
     * 执行所有已注册的任务
     */
    fun start() {
        if (isInitialized) {
            Log.w(TAG, "StartupManager already started")
            return
        }
        
        // 按优先级排序
        tasks.sortBy { it.priority }
        
        Log.i(TAG, "Starting ${tasks.size} startup tasks...")
        
        tasks.forEach { task ->
            try {
                Log.d(TAG, "Executing task: ${task.name}")
                task.create()
                Log.d(TAG, "Task completed: ${task.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Task failed: ${task.name}", e)
            }
        }
        
        Log.i(TAG, "All startup tasks completed")
        isInitialized = true
    }
}
