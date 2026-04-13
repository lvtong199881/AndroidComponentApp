package com.mohanlv.network.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code") val code: Int = 0,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: T? = null
) {
    fun isSuccess(): Boolean = code == 0 || code == 200
    fun needLogin(): Boolean = code == 401
}
