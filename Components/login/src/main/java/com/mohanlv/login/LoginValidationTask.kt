package com.mohanlv.login

import android.app.Application
import com.mohanlv.login.vm.LoginState
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.startup.StartupTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import com.mohanlv.startup.annotation.InitTask

/**
 * 登录状态校验任务
 * 在应用启动时校验本地登录状态是否有效
 */
@InitTask(key = "login", priority = 250)
class LoginValidationTask(private val application: Application) : StartupTask {

    override val name: String = "LoginValidationTask"

    override val priority: Int = 150  // 在 NetworkStartupTask 之后执行

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun create() {
        // 恢复本地登录状态
        LoginState.restore(
            userId = LoginPrefs.userId,
            username = LoginPrefs.username ?: "",
            nickname = LoginPrefs.nickname
        )

        // 如果本地未登录，不需要校验
        if (!LoginPrefs.isLogin) {
            return
        }

        // 异步校验登录状态
        scope.launch {
            try {
                val apiService = NetworkManager.createApi(ApiService::class.java)
                val response = apiService.getCoinInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null || body.needLogin()) {
                        // 登录状态无效，清除本地状态
                        logW("LoginValidationTask::登录状态已失效，清除本地状态")
                        clearLoginState()
                    }
                    // 登录有效，不需要处理
                }
            } catch (e: Exception) {
                // 网络错误，不清除本地状态，等用户手动刷新
                logE("LoginValidationTask::登录状态校验失败", e)
            }
        }
    }

    private fun clearLoginState() {
        LoginState.clear()
    }
}
