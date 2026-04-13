package com.mohanlv.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohanlv.login.model.LoginResult
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 测试账号
    companion object {
        const val TEST_USERNAME = "test"
        const val TEST_PASSWORD = "123456"
        const val TEST_TOKEN = "test_token_12345"
        const val TEST_USER_ID = "user_001"
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 测试账号直接返回成功
                if (username == TEST_USERNAME && password == TEST_PASSWORD) {
                    _loginResult.value = LoginResult.Success(
                        token = TEST_TOKEN,
                        userId = TEST_USER_ID
                    )
                    _isLoading.value = false
                    return@launch
                }

                val params = mapOf("username" to username, "password" to password)
                val response = apiService.login(params)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 0) {
                        _loginResult.value = LoginResult.Success(
                            token = body.data?.token ?: "",
                            userId = body.data?.userId ?: ""
                        )
                    } else {
                        _loginResult.value = LoginResult.Error(body?.message ?: "登录失败")
                    }
                } else {
                    _loginResult.value = LoginResult.Error("网络错误: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "未知错误")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
