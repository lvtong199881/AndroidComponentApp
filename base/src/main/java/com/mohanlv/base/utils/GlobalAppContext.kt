package com.mohanlv.base.utils

import android.app.Application

/**
 * 全局 Application 引用
 * 在 Application.onCreate() 中初始化
 */
object GlobalAppContext {

    private var application: Application? = null

    fun init(application: Application) {
        this.application = application
    }

    fun get(): Application? = application
}