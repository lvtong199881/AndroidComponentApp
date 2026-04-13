package com.mohanlv.router.annotation

/**
 * 路由注解，用于标记需要注册的页面
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Route(
    val path: String,
    val description: String = ""
)
