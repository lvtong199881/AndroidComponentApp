package com.mohanlv.base.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * SharedPreferences 包装器
 * 仅负责底层数据存取，不承接任何业务逻辑
 */
object SPUtils {

    private const val SP_NAME = "app_config"

    private val sp: SharedPreferences?
        get() = GlobalAppContext.get()?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun getString(key: String, default: String? = null): String? =
        sp?.getString(key, default) ?: default

    fun putString(key: String, value: String?) {
        sp?.edit { putString(key, value) }
    }

    fun getInt(key: String, default: Int = 0): Int = sp?.getInt(key, default) ?: default
    fun putInt(key: String, value: Int) {
        sp?.edit { putInt(key, value) }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        sp?.getBoolean(key, default) ?: default

    fun putBoolean(key: String, value: Boolean) {
        sp?.edit { putBoolean(key, value) }
    }

    private fun remove(key: String) {
        sp?.edit { remove(key) }
    }

    fun clear() {
        sp?.edit { clear() }
    }

    // ========== 通用工具字段（按需扩展）==========
    // 所有业务相关的 SP 操作，请放在对应模块的 Prefs 类中
}