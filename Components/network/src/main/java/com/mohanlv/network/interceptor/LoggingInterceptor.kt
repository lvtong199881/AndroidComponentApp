package com.mohanlv.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val logging = HttpLoggingInterceptor { Log.d("NetworkLog", it) }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return chain.proceed(chain.request())
    }
}
