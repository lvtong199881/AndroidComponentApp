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

// CI verification v2: test file-based API response parsing

// CI verification v3: test squash merge commit detection

// CI verification v4: test bash -c Python invocation

// CI verification v5: test base64-encoded Python script

// CI verification v6: test compact base64 Python script

// CI verification v7: test inline except Python script

// CI verification v8: simple Python parser without try-except

// CI verification v9: handle both list and error dict responses

// CI verification v10: move GITHUB_TOKEN to job env level

// CI verification v11: retry on 409 conflict
