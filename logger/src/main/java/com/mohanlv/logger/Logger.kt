package com.mohanlv.logger

import android.util.Log

/**
 * Logger 对外 API
 *
 * 用法示例：
 *
 * // 初始化（在 Application.onCreate 中调用）
 * Logger.init()
 *
 * // 写日志
 * Logger.d("MainActivity", "用户点击了按钮")
 * Logger.i("NetworkHelper", "请求成功")
 * Logger.e("DataRepository", "数据库异常", throwable)
 *
 * // 关闭（可选，一般在闪退时自动调用）
 * Logger.close()
 */
object Logger {
    
    private var initialized = false
    
    fun init() {
        if (initialized) return
        
        // 启动后台写入线程
        LoggerWriter.instance.start()
        
        // 启动时清理过期文件
        LoggerWriter.instance.cleanExpiredFiles()
        
        initialized = true
        Logger.d("Logger", "Logger initialized, logDir=${LoggerConfig.logDir}")
    }
    
    fun close() {
        LoggerWriter.instance.stop()
        initialized = false
    }
    
    private fun log(level: LogEntry.Level, tag: String, message: String, throwable: Throwable? = null) {
        if (!LoggerConfig.enable) return
        
        val fullMessage = if (throwable != null) {
            "$message\n${Log.getStackTraceString(throwable)}"
        } else {
            message
        }
        
        val entry = LogEntry(
            level = level,
            tag = tag,
            message = fullMessage,
            thread = Thread.currentThread().name
        )
        
        // 打印到 Logcat
        if (LoggerConfig.printToLogcat) {
            when (level) {
                LogEntry.Level.VERBOSE -> Log.v(tag, message)
                LogEntry.Level.DEBUG -> Log.d(tag, message)
                LogEntry.Level.INFO -> Log.i(tag, message)
                LogEntry.Level.WARN -> Log.w(tag, message)
                LogEntry.Level.ERROR -> Log.e(tag, message, throwable)
            }
        }
        
        // 写入队列
        if (throwable != null && throwable is RuntimeException) {
            // crash 日志同步写，不走队列
            LoggerWriter.instance.enqueueBlocking(entry)
        } else {
            LoggerWriter.instance.enqueue(entry)
        }
    }
    
    fun v(tag: String, message: String) = log(LogEntry.Level.VERBOSE, tag, message)
    fun d(tag: String, message: String) = log(LogEntry.Level.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogEntry.Level.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogEntry.Level.WARN, tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) = log(LogEntry.Level.ERROR, tag, message, throwable)
}
