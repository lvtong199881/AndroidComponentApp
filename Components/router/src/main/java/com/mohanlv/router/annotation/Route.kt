package com.mohanlv.router.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Route(
    val path: String,
    val description: String = "",
    val needLogin: Boolean = false
)
