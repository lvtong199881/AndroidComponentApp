package com.mohanlv.router

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.ConcurrentHashMap

/**
 * 路由管理器
 */
object RouterManager {
    
    private val routes = ConcurrentHashMap<String, () -> Fragment>()
    private var isInitialized = false
    private var containerId: Int = 0
    var currentActivity: FragmentActivity? = null
    
    fun init(context: Context, containerId: Int = 0) {
        if (isInitialized) return
        this.containerId = containerId
        isInitialized = true
        
        // 使用 APT 生成的路由表注册所有路由
        RouteTable.registerAll(this)
    }
    
    /**
     * 内部注册方法，供生成的 RouteTable 调用
     */
    fun registerInternal(path: String, provider: () -> Fragment) {
        routes[path] = provider
    }
    
    fun register(path: String, provider: () -> Fragment) {
        routes[path] = provider
    }
    
    fun navigate(path: String) {
        val provider = routes[path]
        if (provider == null) {
            throw IllegalStateException("Route not found: $path, registered routes: ${routes.keys}")
        }
        
        val activity = currentActivity
        if (activity == null) {
            throw IllegalStateException("Activity is null")
        }
        
        val fragment = provider()
        activity.supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }
    
    fun getFragment(path: String): Fragment? {
        return routes[path]?.invoke()
    }
    
    fun isRouteExists(path: String): Boolean = routes.containsKey(path)
}
