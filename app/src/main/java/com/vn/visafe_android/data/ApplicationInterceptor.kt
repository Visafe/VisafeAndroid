package com.vn.visafe_android.data

import android.content.Context
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.ViSafeApp
import okhttp3.Interceptor
import okhttp3.Response

class ApplicationInterceptor(context: Context) : Interceptor {
    private val pref = ViSafeApp().getPreference()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Content-type", "application/json")
            val token = pref.getString(PreferenceKey.AUTH_TOKEN)
            token?.let {
                if (it.isNotBlank()) {
                    addHeader("Authorization", token)
                }
            }
            val method = original.method
            val body = original.body
            method(method, body)
        }

        return chain.proceed(requestBuilder.build())
    }
}

class ApplicationInterceptorWithoutToken(context: Context) : Interceptor {
    private val pref = ViSafeApp().getPreference()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Content-type", "application/json")
            val method = original.method
            val body = original.body
            method(method, body)
        }

        return chain.proceed(requestBuilder.build())
    }
}

class ApplicationInterceptorWithBearer : Interceptor {
    private val pref = ViSafeApp().getPreference()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Content-type", "application/json")
            val token = pref.getString(PreferenceKey.AUTH_TOKEN)
            token.let {
                if (it.isNotBlank()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            val method = original.method
            val body = original.body
            method(method, body)
        }

        return chain.proceed(requestBuilder.build())
    }
}
