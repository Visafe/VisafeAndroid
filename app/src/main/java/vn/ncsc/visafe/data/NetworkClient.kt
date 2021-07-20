package vn.ncsc.visafe.data

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import vn.ncsc.visafe.BuildConfig
import java.util.concurrent.TimeUnit

private const val API_READ_TIMEOUT: Long = 160
private const val API_CONNECT_TIMEOUT: Long = 160

class NetworkClient {
    companion object {
        const val CODE_INCORRECT_CLASSIFY: Int = 422
        const val CODE_CREATED: Int = 201
        const val CODE_SUCCESS: Int = 200
        const val CODE_EXISTS_ACCOUNT: Int = 400
        const val CODE_NOT_EXISTS_ACCOUNT: Int = 404
        const val CODE_ACCOUNT_ALREADY_ACTIVE = 409
        const val CODE_DELETE_SUCCESS: Int = 204
        const val CODE_LIMIT_PAYMENT = 402
        const val CODE_SEVER_ERROR = 502
        const val CODE_TIMEOUT_SESSION = 401
        val ERROR_CODE =
            listOf(403, 406, 409, 424, 502, 500)
        const val URL_ROOT = "https://staging.visafe.vn/api/v1/"
        const val DOMAIN = "https://dns-staging.visafe.vn/dns-query/"

    }

    fun client(context: Context): ApiService {
        return provideApiService(provideRetrofit(provideHttpClient(context)))
    }

    fun clientWithBearer(context: Context): ApiService {
        return provideApiService(provideRetrofit(provideHttpClientGen2FA(context)))
    }

    fun clientWithoutRootUrl(context: Context, url: String): ApiService {
        return provideApiService(provideRetrofit(provideHttpClient(context), url))

    }

    fun clientWithoutToken(context: Context): ApiService {
        return provideApiService(provideRetrofit(provideHttpClientWithoutToken(context)))
    }

    private fun provideHttpClientGen2FA(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApplicationInterceptorWithBearer())
            .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ResponseInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })

            .build()
    }

    private fun provideHttpClientWithoutToken(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApplicationInterceptorWithoutToken(context))
            .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ResponseInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })

            .build()
    }

    private fun provideHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApplicationInterceptor(context))
            .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ResponseInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })

            .build()
    }

    private fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL_ROOT)
            .build()
    }


    private fun provideRetrofit(client: OkHttpClient, url: String): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
    }

    private fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}