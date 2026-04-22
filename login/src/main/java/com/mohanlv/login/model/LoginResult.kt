package com.mohanlv.login.model

/**
 * 登录结果
 */
sealed class LoginResult {
    data class Success(
        val userId: Int,
        val username: String,
        val nickname: String?,
        val token: String?,
        val coinCount: Int = 0
    ) : LoginResult()

    data class Error(val message: String) : LoginResult()
}

/**
 * 注册结果
 */
sealed class RegisterResult {
    data class Success(
        val userId: Int,
        val username: String,
        val nickname: String?
    ) : RegisterResult()

    data class Error(val message: String) : RegisterResult()
}
