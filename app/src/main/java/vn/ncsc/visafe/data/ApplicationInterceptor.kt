package vn.ncsc.visafe.data

import android.content.Context
import vn.ncsc.visafe.utils.PreferenceKey
import okhttp3.Interceptor
import okhttp3.Response
import vn.ncsc.visafe.ViSafeApp

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

class ApplicationInterceptorWithCheckBotnet : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Content-type", "application/json")
//            addHeader("Origin", "https://congcu.khonggianmang.vn")
//            addHeader("Referer", "https://congcu.khonggianmang.vn/")
            val method = original.method
            val body = original.body
            method(method, body)
        }

        return chain.proceed(requestBuilder.build())
    }
}
