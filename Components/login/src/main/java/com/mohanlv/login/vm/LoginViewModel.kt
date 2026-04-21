package com.mohanlv.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohanlv.login.model.LoginResult
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.base.utils.SPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * 登录 ViewModel
 * 使用 WanAndroid 真实 API
 */
class LoginViewModel : ViewModel() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("用户名和密码不能为空")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.login(username, password)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess()) {
                        val data = body.data
                        _loginResult.value = LoginResult.Success(
                            userId = data?.id ?: 0,
                            username = data?.username ?: username,
                            nickname = data?.nickname,
                            token = data?.token,
                            coinCount = 0 // 登录后单独获取
                        )
                    } else {
                        _loginResult.value = LoginResult.Error(
                            body?.errorMsg ?: "登录失败，请检查用户名和密码"
                        )
                    }
                } else {
                    android.util.Log.e("LoginViewModel", "登录失败，响应码: ${response.code()}")
                    _loginResult.value = LoginResult.Error("网络错误: ${response.code()}")
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve") == true -> "网络连接失败，请检查网络设置"
                    e.message?.contains("timeout") == true -> "请求超时，请稍后重试"
                    e.message?.contains("CERTIFICATE") == true -> "证书验证失败"
                    else -> e.message ?: "网络请求失败"
                }
                _loginResult.value = LoginResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

}

/**
 * 登录状态变化监听器
 */
interface OnLoginStateChangedListener {
    fun onLoginSuccess(userId: Int, username: String, nickname: String?)
    fun onLogout()
}

/**
 * 登录状态管理器
 */
object LoginState {
    var isLoggedIn: Boolean = false
        private set

    var userId: Int = 0
        private set

    var username: String = ""
        private set

    var nickname: String = ""
        private set

    var token: String = ""
        private set

    // 登录状态监听器列表（使用弱引用避免内存泄漏）
    private val listeners = mutableListOf<WeakReference<OnLoginStateChangedListener>>()

    /**
     * 注册登录状态监听器
     */
    fun addListener(listener: OnLoginStateChangedListener) {
        if (listeners.none { it.get() === listener }) {
            listeners.add(WeakReference(listener))
        }
    }

    /**
     * 移除登录状态监听器
     */
    fun removeListener(listener: OnLoginStateChangedListener) {
        listeners.removeAll { it.get() === listener }
    }

    /**
     * 退出登录
     * 调用 logout 接口，等接口返回后再清除本地状态并通知监听器
     */
    fun logout() {
        // 已退登则不再调接口
        if (!isLoggedIn) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                NetworkManager.createApi(ApiService::class.java).logout()
            } catch (_: Exception) {
                // 忽略退出接口错误
            }
            // 等接口返回后再清除本地状态
            clear()
        }
    }

    /**
     * 清除登录状态并通知监听器
     */
    fun clear() {
        isLoggedIn = false
        userId = 0
        username = ""
        nickname = ""
        token = ""

        // 清除本地持久化状态
        SPUtils.clear()

        // 通知所有监听器（清理已回收的弱引用）
        listeners.removeAll { it.get() == null }
        listeners.forEach { it.get()?.onLogout() }
    }

    /**
     * 保存登录状态并通知监听器
     */
    fun saveLogin(userId: Int, username: String, nickname: String?, token: String?) {
        this.isLoggedIn = true
        this.userId = userId
        this.username = username
        this.nickname = nickname ?: ""
        this.token = token ?: ""

        // 通知所有监听器（清理已回收的弱引用）
        listeners.removeAll { it.get() == null }
        listeners.forEach { it.get()?.onLoginSuccess(userId, username, nickname) }
    }

    /**
     * 从外部数据恢复登录状态（用于 App 重启后从 SPUtils 恢复）
     * 注意：恢复状态不触发回调，因为这不是新的登录/登出动作
     */
    fun restore(userId: Int, username: String, nickname: String?) {
        this.isLoggedIn = true
        this.userId = userId
        this.username = username
        this.nickname = nickname ?: ""
    }
}
