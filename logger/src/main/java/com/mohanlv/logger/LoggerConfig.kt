package com.mohanlv.logger

import java.io.File

/**
 * Logger 配置
 */
object LoggerConfig {
    
    /** 日志目录，默认在 App 私有目录下 */
    var logDir: File = File(
        System.getProperty("user.home"),
        "Android/data/com.mohanlv.app/files/logs"
    )
    
    /** 日志文件名前缀 */
    var filePrefix: String = "app"
    
    /** 单文件大小上限，默认 10MB */
    var maxFileSize: Long = 10 * 1024 * 1024
    
    /** 日志保留天数，默认 7 天 */
    var retainDays: Int = 7
    
    /** 是否开启日志输出，默认 true */
    var enable: Boolean = true
    
    /** 是否打印到 Logcat，默认 true */
    var printToLogcat: Boolean = true
    
    /** 异步队列容量，默认 500 */
    var queueCapacity: Int = 500
    
    /** 队列积压超过此数量则强制刷盘，默认 100 */
    var flushThreshold: Int = 100
}
