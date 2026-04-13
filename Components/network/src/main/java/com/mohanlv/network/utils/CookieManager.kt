package com.mohanlv.network.utils

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * Cookie 管理器
 * 用于保存和发送登录态 Cookie
 */
object AppCookieManager : CookieJar {

    private const val SP_NAME = "cookie_store"
    private const val KEY_COOKIES = "cookies"
    
    private lateinit var sp: SharedPreferences
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        loadCookies()
    }

    private fun loadCookies() {
        val cookiesString = sp.getString(KEY_COOKIES, null) ?: return
        try {
            val cookieNames = cookiesString.split(";")
            for (name in cookieNames) {
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) {
                    cookieStore["wanandroid.com"] = cookieStore["wanandroid.com"]?.plus(
                        Cookie.Builder().name(trimmed).value("").domain("wanandroid.com").build()
                    ) ?: listOf(
                        Cookie.Builder().name(trimmed).value("").domain("wanandroid.com").build()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveCookies(cookies: List<Cookie>) {
        cookieStore["wanandroid.com"] = cookies
        // 保存 cookie names
        val cookieString = cookies.joinToString(";") { "${it.name}=${it.value}" }
        sp.edit().putString(KEY_COOKIES, cookieString).apply()
    }

    fun saveCookiesFromHeaders(headers: okhttp3.Headers) {
        val cookieValues = headers.values("Set-Cookie")
        if (cookieValues.isEmpty()) return
        
        val cookies = cookieValues.mapNotNull { cookieStr ->
            try {
                Cookie.parse("https://wanandroid.com/".toHttpUrlOrNull()!!, cookieStr)
            } catch (e: Exception) {
                null
            }
        }
        
        if (cookies.isNotEmpty()) {
            saveCookies(cookies)
        }
    }

    fun clearCookies() {
        cookieStore.clear()
        sp.edit().remove(KEY_COOKIES).apply()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (url.host.contains("wanandroid")) {
            saveCookies(cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }
}
