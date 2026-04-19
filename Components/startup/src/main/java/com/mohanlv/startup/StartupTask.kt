package com.mohanlv.startup

/**
 * 模块初始化任务的接口
 * 每个模块可以实现这个接口来注册自己的初始化逻辑
 */
interface StartupTask {
    
    /**
     * 任务名称，用于调试
     */
    val name: String
    
    /**
     * 任务优先级，数字越小越先执行
     */
    val priority: Int
        get() = 0
    
    /**
     * 执行初始化逻辑
     */
    fun create(): Unit
}
