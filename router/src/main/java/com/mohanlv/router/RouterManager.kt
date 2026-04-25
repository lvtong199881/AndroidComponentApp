package com.mohanlv.router

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.ConcurrentHashMap

/**
 * 路由管理器
 * 支持 oneandroid:// scheme，支持外部唤起和内部跳转
 */
object RouterManager {
    
    private const val TAG = "RouterManager"
    
    // 内部注册表，key 为路径（不含 scheme）
    private val routes = ConcurrentHashMap<String, () -> Fragment>()
    private var isInitialized = false
    var currentActivity: FragmentActivity? = null
    
    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
        
        // 使用 ServiceLoader 加载所有 RouteCollector 实现类并注册路由
        loadRouteCollectors()
    }
    
    /**
     * 内部注册方法，供 RouteCollector 调用
     * @param clazz Fragment class
     */
    fun registerInternal(path: String, clazz: Class<out Fragment>) {
        routes[path] = { clazz.getDeclaredConstructor().newInstance() }
    }
    
    /**
     * 注册路由（供外部调用）
     */
    fun register(path: String, provider: () -> Fragment) {
        routes[path] = provider
    }
    
    /**
     * 加载所有 RouteCollector 实现类并注册路由
     */
    private fun loadRouteCollectors() {
        // 使用 RouterManager 的 classloader，避免 includeBuild 模式下的 classloader 隔离问题
        val serviceLoader = java.util.ServiceLoader.load(RouteCollector::class.java, RouterManager::class.java.classLoader)
        for (collector in serviceLoader) {
            for ((path, className) in collector.getRoutes()) {
                try {
                    val clazz = Class.forName(className, true, RouterManager::class.java.classLoader).asSubclass(Fragment::class.java)
                    registerInternal(path, clazz)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register route: $path -> $className", e)
                }
            }
        }
    }
    
    /**
     * 页面跳转
     * @param path 路径，支持：
     *   - 完整 URL：oneandroid://home/main
     *   - 内部路径：/home/main
     *   - 带参数：oneandroid://home/main?key=value
     */
    fun navigate(path: String, addToBackStack: Boolean = true) {
        navigate(path, null, addToBackStack)
    }
    
    fun navigate(path: String, args: Bundle?, addToBackStack: Boolean = true) {
        // 提取内部路径（去掉 scheme）
        val internalPath = extractInternalPath(path)
        val queryParams = extractQueryParams(path)
        
        // 合并 args 和 query params
        val mergedArgs = Bundle()
        args?.let { mergedArgs.putAll(it) }
        queryParams.forEach { (key, value) ->
            if (!mergedArgs.containsKey(key)) {
                mergedArgs.putString(key, value)
            }
        }
        
        val provider = routes[internalPath]
        if (provider == null) {
            throw IllegalStateException("Route not found: $internalPath, registered routes: ${routes.keys}")
        }
        
        val activity = currentActivity
        if (activity == null) {
            throw IllegalStateException("Activity is null")
        }
        
        val fragment = provider()
        
        // 传递参数到 Fragment
        if (mergedArgs.size() > 0) {
            fragment.arguments = mergedArgs
        }
        
        val transaction = activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                com.mohanlv.router.R.anim.slide_in_right,
                com.mohanlv.router.R.anim.slide_out_left,
                com.mohanlv.router.R.anim.slide_in_left,
                com.mohanlv.router.R.anim.slide_out_right
            )
            .add(android.R.id.content, fragment, internalPath)
        
        if (addToBackStack) {
            transaction.addToBackStack(internalPath)
        }
        
        transaction.commit()
    }
    
    /**
     * 处理外部 Intent（来自 deep link）
     */
    fun handleIntent(intent: Intent): Boolean {
        val data = intent.data ?: return false
        val scheme = data.scheme
        val host = data.host
        val path = data.path ?: return false
        
        if (scheme != RoutePath.SCHEME) return false
        
        // 构建内部路径
        val internalPath = "$host$path"
        
        // 提取 query 参数
        val args = Bundle()
        data.queryParameterNames?.forEach { param ->
            args.putString(param, data.getQueryParameter(param))
        }
        
        navigate(internalPath, args)
        return true
    }
    
    /**
     * 从完整 URL 提取内部路径
     * oneandroid://home/main -> /home/main
     */
    private fun extractInternalPath(url: String): String {
        return if (url.contains("://")) {
            url.substringAfter("://")
        } else {
            url
        }.substringBefore("?")
    }
    
    /**
     * 从 URL 提取 query 参数
     */
    private fun extractQueryParams(url: String): Map<String, String> {
        if (!url.contains("?")) return emptyMap()
        
        val queryString = url.substringAfter("?")
        return queryString.split("&").mapNotNull { param ->
            val parts = param.split("=")
            if (parts.size == 2) {
                parts[0] to parts[1]
            } else null
        }.toMap()
    }
    
    fun popBackStack() {
        currentActivity?.supportFragmentManager?.popBackStack()
    }
    
    fun getFragment(path: String): Fragment? {
        val internalPath = extractInternalPath(path)
        return routes[internalPath]?.invoke()
    }
    
    fun isRouteExists(path: String): Boolean {
        val internalPath = extractInternalPath(path)
        return routes.containsKey(internalPath)
    }
    
    /**
     * 获取所有已注册的路由
     */
    fun getRegisteredRoutes(): Set<String> = routes.keys.toSet()
}
