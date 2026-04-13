package com.mohanlv.network.api

import com.mohanlv.network.model.BaseResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body params: Map<String, String>): Response<BaseResponse<LoginResponse>>
    
    @GET("user/info")
    suspend fun getUserInfo(): Response<BaseResponse<UserInfo>>
    
    @POST("user/logout")
    suspend fun logout(): Response<BaseResponse<Unit>>
}

data class LoginResponse(val token: String, val userId: String, val expiresIn: Long)
data class UserInfo(val id: String, val username: String, val avatar: String?, val email: String?)
