package com.mohanlv.startup.annotation

/**
 * 启动任务注解，标记在实现 StartupTask 接口的类上
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InitTask(
    /**
     * 任务唯一标识 key
     */
    val key: String,
    /**
     * 任务优先级，数字越小越先执行
     */
    val priority: Int = 0
)
