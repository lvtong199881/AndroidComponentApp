package com.mohanlv.router

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mohanlv.router.RoutePath.HOME_CONTAINER
import com.mohanlv.router.RoutePath.LOGIN
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
        
        // 自动扫描并注册所有带 @Route 注解的 Fragment
        scanAndRegister(context)
    }
    
    /**
     * 自动扫描所有带 @Route 注解的 Fragment
     */
    private fun scanAndRegister(context: Context) {
        try {
            val packageName = context.packageName
            scanFragmentsInDex(context, packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // 如果扫描失败，手动注册
        if (routes.isEmpty()) {
            registerAllFragments()
        }
    }
    
    private fun scanFragmentsInDex(context: Context, packageName: String) {
        try {
            val classLoader = context.classLoader
            val pathList = getBaseDexPathList(classLoader) ?: return
            
            val elementsField = pathList.javaClass.getDeclaredField("dexElements")
            elementsField.isAccessible = true
            val dexElements = elementsField.get(pathList) as Array<*>
            
            for (element in dexElements) {
                try {
                    val dexFile = element?.javaClass?.getDeclaredMethod("getDexFile")
                        ?.invoke(element) as? dalvik.system.DexFile
                    
                    if (dexFile != null) {
                        val entries = dexFile.entries()
                        while (entries.hasMoreElements()) {
                            val className = entries.nextElement()
                            if (className.startsWith(packageName) && className.contains(".ui.")) {
                                try {
                                    val clazz = Class.forName(className, false, classLoader)
                                    val route = clazz.getAnnotation(com.mohanlv.router.annotation.Route::class.java)
                                    if (route != null && Fragment::class.java.isAssignableFrom(clazz)) {
                                        register(route.path) {
                                            clazz.newInstance() as Fragment
                                        }
                                    }
                                } catch (e: Exception) {
                                    // 忽略
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // 忽略
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getBaseDexPathList(classLoader: ClassLoader): Any? {
        return try {
            val baseDexPathList = classLoader.javaClass.getDeclaredField("pathList")
            baseDexPathList.isAccessible = true
            baseDexPathList.get(classLoader)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun registerAllFragments() {
        // 手动注册所有 Fragment
        try {
            register(LOGIN) { 
                Class.forName("com.mohanlv.login.ui.LoginFragment").newInstance() as Fragment 
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
        
        try {
            register(HOME_CONTAINER) { 
                Class.forName("com.mohanlv.home.ui.container.HomeContainerFragment").newInstance() as Fragment 
            }
        } catch (e: Exception) { 
            e.printStackTrace()
        }
    }
    
    fun register(path: String, provider: () -> Fragment) {
        routes[path] = provider
    }
    
    fun navigate(path: String) {
        val provider = routes[path]
        if (provider == null) {
            throw IllegalStateException("Route not found: $path, registered routes: ${routes.keys}")
        }
        
        // 需要从 Activity 获取 context，而不是从 Fragment
        val activity = com.mohanlv.router.RouterManager.currentActivity
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
    
    // 扩展：获取 declared field
    private fun Any.javaDeclaredField(name: String): java.lang.reflect.Field {
        val field = javaClass.getDeclaredField(name)
        field.isAccessible = true
        return field
    }
}
