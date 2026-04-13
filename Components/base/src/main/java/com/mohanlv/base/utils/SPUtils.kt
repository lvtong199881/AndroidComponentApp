package com.mohanlv.base.utils

import android.content.Context
import android.content.SharedPreferences

object SPUtils {
    
    private const val SP_NAME = "app_config"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_LOGIN = "is_login"
    
    private lateinit var sp: SharedPreferences
    
    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }
    
    var token: String?
        get() = sp.getString(KEY_TOKEN, null)
        set(value) = sp.edit().putString(KEY_TOKEN, value).apply()
    
    var userId: String?
        get() = sp.getString(KEY_USER_ID, null)
        set(value) = sp.edit().putString(KEY_USER_ID, value).apply()
    
    var isLogin: Boolean
        get() = sp.getBoolean(KEY_IS_LOGIN, false)
        set(value) = sp.edit().putBoolean(KEY_IS_LOGIN, value).apply()
    
    fun clear() {
        sp.edit().clear().apply()
    }
}
