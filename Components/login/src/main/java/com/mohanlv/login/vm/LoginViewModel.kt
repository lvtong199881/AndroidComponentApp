package com.mohanlv.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohanlv.login.model.LoginResult
import com.mohanlv.login.model.RegisterResult
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.network.model.LoginRequest
import com.mohanlv.network.model.RegisterRequest
import kotlinx.coroutines.launch

/**
 * 登录/注册 ViewModel
 * 使用 WanAndroid 真实 API
 */
class LoginViewModel : ViewModel() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

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

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     */
    fun register(username: String, password: String, repassword: String) {
        if (username.isBlank() || password.isBlank()) {
            _registerResult.value = RegisterResult.Error("用户名和密码不能为空")
            return
        }

        if (password != repassword) {
            _registerResult.value = RegisterResult.Error("两次密码输入不一致")
            return
        }

        if (password.length < 6) {
            _registerResult.value = RegisterResult.Error("密码长度不能少于6位")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.register(username, password, repassword)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess()) {
                        val data = body.data
                        _registerResult.value = RegisterResult.Success(
                            userId = data?.id ?: 0,
                            username = data?.username ?: username,
                            nickname = data?.nickname
                        )
                    } else {
                        _registerResult.value = RegisterResult.Error(
                            body?.errorMsg ?: "注册失败"
                        )
                    }
                } else {
                    _registerResult.value = RegisterResult.Error("网络错误: ${response.code()}")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error(e.message ?: "网络请求失败")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 检查是否已登录
     */
    fun checkLoginState(): Boolean {
        return LoginState.isLoggedIn
    }
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

    fun saveLogin(userId: Int, username: String, nickname: String?, token: String?) {
        this.isLoggedIn = true
        this.userId = userId
        this.username = username
        this.nickname = nickname ?: ""
        this.token = token ?: ""
    }

    fun clear() {
        isLoggedIn = false
        userId = 0
        username = ""
        nickname = ""
        token = ""
    }

    /**
     * 从外部数据恢复登录状态（用于 App 重启后从 SPUtils 恢复）
     */
    fun restore(userId: Int, username: String, nickname: String?) {
        this.isLoggedIn = true
        this.userId = userId
        this.username = username
        this.nickname = nickname ?: ""
    }
}
