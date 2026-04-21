package com.mohanlv.base.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * SharedPreferences 包装器
 * 仅负责底层数据存取，不承接任何业务逻辑
 */
object SPUtils {
    
    private const val SP_NAME = "app_config"
    
    private val sp: SharedPreferences?
        get() = GlobalAppContext.get()?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    
    private fun getString(key: String, default: String? = null): String? = sp?.getString(key, default) ?: default
    private fun putString(key: String, value: String?) { sp?.edit { putString(key, value) } }
    private fun getInt(key: String, default: Int = 0): Int = sp?.getInt(key, default) ?: default
    private fun putInt(key: String, value: Int) { sp?.edit { putInt(key, value) } }
    private fun getBoolean(key: String, default: Boolean = false): Boolean = sp?.getBoolean(key, default) ?: default
    private fun putBoolean(key: String, value: Boolean) { sp?.edit { putBoolean(key, value) } }
    private fun remove(key: String) { sp?.edit { remove(key) } }
    fun clear() { sp?.edit { clear() } }
    
    // ========== 登录相关字段 ==========
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_IS_LOGIN = "is_login"
    private const val KEY_LOGIN_HISTORY = "login_history"
    private const val KEY_PASSWORD = "password"
    
    var token: String?
        get() = getString(KEY_TOKEN)
        set(value) = putString(KEY_TOKEN, value)
    
    var userId: Int
        get() = getInt(KEY_USER_ID)
        set(value) = putInt(KEY_USER_ID, value)
    
    var username: String?
        get() = getString(KEY_USERNAME)
        set(value) = putString(KEY_USERNAME, value)
    
    var nickname: String?
        get() = getString(KEY_NICKNAME)
        set(value) = putString(KEY_NICKNAME, value)
    
    var isLogin: Boolean
        get() = getBoolean(KEY_IS_LOGIN)
        set(value) = putBoolean(KEY_IS_LOGIN, value)
    
    var password: String?
        get() = getString(KEY_PASSWORD)
        set(value) = putString(KEY_PASSWORD, value)
    
    var loginHistory: List<Account>
        get() {
            val json = getString(KEY_LOGIN_HISTORY) ?: return emptyList()
            return try {
                Gson().fromJson(json, object : TypeToken<List<Account>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            putString(KEY_LOGIN_HISTORY, Gson().toJson(value))
        }
    
    data class Account(val username: String, val password: String)
}