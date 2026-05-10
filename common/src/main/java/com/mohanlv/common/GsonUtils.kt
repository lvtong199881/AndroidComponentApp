package com.mohanlv.common

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * JSON 序列化工具类
 */
object GsonUtils {

    private val gson = Gson()

    /**
     * 对象序列化为 JSON 字符串
     */
    fun <T> toJson(obj: T?): String? {
        obj ?: return null
        return try {
            gson.toJson(obj)
        } catch (e: Exception) {
            logE("序列化失败", e)
            null
        }
    }

    /**
     * JSON 字符串反序列化为对象（指定 Class）
     */
    fun <T> fromJson(json: String?, classOfT: Class<T>): T? {
        if (json.isNullOrEmpty()) return null
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            logE("反序列化失败", e)
            null
        }
    }

    /**
     * JSON 字符串反序列化为对象（指定 Type）
     */
    fun <T> fromJson(json: String?, typeOfT: Type): T? {
        if (json.isNullOrEmpty()) return null
        return try {
            gson.fromJson(json, typeOfT)
        } catch (e: Exception) {
            logE("反序列化失败", e)
            null
        }
    }
}