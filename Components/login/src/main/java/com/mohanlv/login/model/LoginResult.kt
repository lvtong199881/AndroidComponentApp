package com.mohanlv.login.model

sealed class LoginResult {
    data class Success(val token: String, val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
