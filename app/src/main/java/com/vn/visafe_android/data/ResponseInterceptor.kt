package com.vn.visafe_android.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}