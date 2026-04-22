package com.mohanlv.router

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 路由参数构建器
 */
class RouteParams private constructor(private val bundle: Bundle) {
    
    companion object {
        fun build(block: Builder.() -> Unit): RouteParams {
            val builder = Builder()
            builder.block()
            return RouteParams(builder.bundle)
        }
    }
    
    class Builder {
        val bundle = Bundle()
        fun putInt(key: String, value: Int) = apply { bundle.putInt(key, value) }
        fun putLong(key: String, value: Long) = apply { bundle.putLong(key, value) }
        fun putString(key: String, value: String) = apply { bundle.putString(key, value) }
        fun putBoolean(key: String, value: Boolean) = apply { bundle.putBoolean(key, value) }
        fun putParcelable(key: String, value: Parcelable) = apply { bundle.putParcelable(key, value) }
        fun putSerializable(key: String, value: Serializable) = apply { bundle.putSerializable(key, value) }
    }
    
    fun getBundle(): Bundle = bundle
    fun getInt(key: String, default: Int = 0) = bundle.getInt(key, default)
    fun getString(key: String, default: String? = null) = bundle.getString(key, default)
    fun getBoolean(key: String, default: Boolean = false) = bundle.getBoolean(key, default)
}
