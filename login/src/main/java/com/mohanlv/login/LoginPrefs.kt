package com.mohanlv.login

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mohanlv.base.utils.SPUtils

/**
 * 登录相关 SharedPreferences 管理
 * 封装所有登录状态的持久化操作
 */
object LoginPrefs {
    
    private const val KEY_PREFIX = "login_"
    
    private fun k(key: String) = KEY_PREFIX + key
    
    var isLogin: Boolean
        get() = SPUtils.getBoolean(k("is_login"))
        set(value) = SPUtils.putBoolean(k("is_login"), value)
    
    var userId: Int
        get() = SPUtils.getInt(k("user_id"))
        set(value) = SPUtils.putInt(k("user_id"), value)
    
    var username: String?
        get() = SPUtils.getString(k("username"))
        set(value) = SPUtils.putString(k("username"), value)
    
    var nickname: String?
        get() = SPUtils.getString(k("nickname"))
        set(value) = SPUtils.putString(k("nickname"), value)
    
    var token: String?
        get() = SPUtils.getString(k("token"))
        set(value) = SPUtils.putString(k("token"), value)
    
    var loginHistory: List<Account>
        get() {
            val json = SPUtils.getString(k("history")) ?: return emptyList()
            return try {
                Gson().fromJson(json, object : TypeToken<List<Account>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            SPUtils.putString(k("history"), Gson().toJson(value))
        }
    
    /**
     * 清除登录数据（保留历史记录）
     */
    fun clear() {
        isLogin = false
        userId = 0
        username = null
        nickname = null
        token = null
    }
    
    data class Account(val username: String, val password: String)
}