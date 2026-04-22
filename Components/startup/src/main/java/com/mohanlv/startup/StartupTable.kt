package com.mohanlv.startup

import android.app.Application

/**
 * 自动生成的启动任务表
 * 由 generate_startup.py 在编译前自动生成
 * 使用反射实例化，避免 startup 模块依赖其他业务模块
 */
object StartupTable {

    /**
     * 收集所有任务实例，按 priority 排序
     */
    fun collectTasks(application: Application, extraParams: Map<String, Any> = emptyMap()): List<StartupTask> {
        val tasks = mutableListOf<StartupTask>()

        // com.mohanlv.base.BaseStartupTask (key=base, priority=0)
        tasks.add(instantiate("com.mohanlv.base.BaseStartupTask", application))

        // com.mohanlv.base.LoggerStartupTask (key=logger, priority=100)
        tasks.add(instantiate("com.mohanlv.base.LoggerStartupTask", application))

        // com.mohanlv.network.NetworkStartupTask (key=network, priority=200)
        tasks.add(instantiate("com.mohanlv.network.NetworkStartupTask", application))

        // com.mohanlv.login.LoginValidationTask (key=login, priority=250)
        tasks.add(instantiate("com.mohanlv.login.LoginValidationTask", application))

        // com.mohanlv.router.RouterStartupTask (key=router, priority=300)
        tasks.add(instantiate("com.mohanlv.router.RouterStartupTask", application))

        // com.mohanlv.reactnative.ReactNativeStartupTask (key=reactnative, priority=400)
        tasks.add(instantiate("com.mohanlv.reactnative.ReactNativeStartupTask", application))

        return tasks
    }

    private fun instantiate(className: String, application: Application): StartupTask {
        return Class.forName(className)
            .getDeclaredConstructor(Application::class.java)
            .newInstance(application) as StartupTask
    }

    private fun instantiateWithInt(className: String, application: Application, intParam: Int): StartupTask {
        return Class.forName(className)
            .getDeclaredConstructor(Application::class.java, Int::class.javaPrimitiveType)
            .newInstance(application, intParam) as StartupTask
    }
}
