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

// CI verification v12: fix 409 retry with PIPESTATUS and grep detection

// CI verification v13: use max-version python script across all API calls

// CI verification v14: fix tag name format for GitHub Release

// CI verification v15: add token to git clone URL in ComponentHostApp update

// CI verification v16: retest CI after network retry

// CI verification v17: comment out ComponentHostApp update (cross-repo token issue)

// CI verification v18: re-enable ComponentHostApp update with GITHUB_TOKEN cross-repo

// CI verification v19: use COMPONENT_HOST_APP_TOKEN for cross-repo push

// CI verification v20: retry CI after PAT propagation

// CI verification v21: fix ${secrets.COMPONENT_HOST_APP_TOKEN} syntax

// CI verification v22: fix env block for COMPONENT_HOST_APP_TOKEN

// CI verification v23: publish all modules when no changes detected

// CI verification v24: retry on 401 Unauthorized
