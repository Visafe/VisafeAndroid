package vn.ncsc.visafe.data

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import vn.ncsc.visafe.BuildConfig
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

private const val API_READ_TIMEOUT: Long = 30
private const val API_CONNECT_TIMEOUT: Long = 30

class NetworkClient {
    companion object {
        const val CODE_INCORRECT_CLASSIFY: Int = 422
        const val CODE_CREATED: Int = 201
        const val CODE_SUCCESS: Int = 200
        const val CODE_EXISTS_ACCOUNT: Int = 400
        const val CODE_NOT_EXISTS_ACCOUNT: Int = 404
        const val CODE_ACCOUNT_ALREADY_ACTIVE = 409
        const val CODE_DELETE_SUCCESS: Int = 204
        const val CODE_406: Int = 406
        const val CODE_403: Int = 403
        const val CODE_LIMIT_PAYMENT = 402
        const val CODE_SEVER_ERROR = 502
        const val CODE_TIMEOUT_SESSION = 401
        val ERROR_CODE =
            listOf(406, 409, 424, 502, 500)
//        const val URL_ROOT = "https://staging.visafe.vn/api/v1/"
//        const val DOMAIN = "https://dns-staging.visafe.vn/dns-query/"
        const val URL_ROOT = "https://app.visafe.vn/api/v1/"
        const val DOMAIN = "https://dns.visafe.vn/dns-query/"

    }

    fun client(context: Context): ApiService {
        return provideApiService(provideRetrofit(provideHttpClient(context)))
    }

    fun clientWithoutToken(context: Context): ApiService {
        return provideApiService(provideRetrofit(provideHttpClientWithoutToken(context)))
    }

    private fun provideHttpClientWithoutToken(context: Context): OkHttpClient {
        val trustAllCerts = getTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .addInterceptor(ApplicationInterceptorWithoutToken(context))
            .callTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ResponseInterceptor(context))
            .sslSocketFactory(sslSocketFactory, (trustAllCerts!![0] as X509TrustManager))
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })

            .build()
    }

    private fun provideHttpClient(context: Context): OkHttpClient {
        val trustAllCerts = getTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .addInterceptor(ApplicationInterceptor(context))
            .callTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ResponseInterceptor(context))
            .sslSocketFactory(sslSocketFactory, (trustAllCerts!![0] as X509TrustManager))
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
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

    private fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private fun getTrustManager(): Array<TrustManager>? {
        return arrayOf<TrustManager>(
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
    }

}