package com.mohanlv.base.utils

import android.app.Application

/**
 * 应用工具类
 */
object AppUtils {

    private var application: Application? = null

    fun init(app: Application) {
        application = app
    }

    fun getApplication(): Application {
        return application ?: throw IllegalStateException("AppUtils not initialized")
    }

    fun getContext() = getApplication().applicationContext

    fun getPackageName(): String = getContext().packageName

    fun getVersionName(): String {
        return try {
            val packageInfo = getContext().packageManager.getPackageInfo(getPackageName(), 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            logE("AppUtils::获取版本号失败", e)
            "1.0.0"
        }
    }
}

// CI verification: test per-module versioning
