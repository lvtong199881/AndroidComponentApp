package com.mohanlv.network.utils

import com.mohanlv.network.model.BaseResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(dispatchers: CoroutineDispatcher = Dispatchers.IO, block: suspend () -> Response<BaseResponse<T>>): Result<BaseResponse<T>> =
    withContext(dispatchers) {
        try {
            val response = block()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ApiException("Request failed: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(ApiException("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(ApiException("Error: ${e.message}", e))
        }
    }

class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
