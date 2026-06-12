package com.example.zoosmartcare.data.network

import android.content.Context
import com.example.zoosmartcare.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private var _tokenManager: TokenManager? = null

    val tokenManager: TokenManager
        get() = _tokenManager ?: throw IllegalStateException("NetworkModule is not initialized. Call init(context) first.")

    fun init(context: Context) {
        if (_tokenManager == null) {
            _tokenManager = TokenManager(context.applicationContext)
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = _tokenManager?.getToken()
        
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val adminApi: AdminApi by lazy {
        retrofit.create(AdminApi::class.java)
    }

    val businessApi: BusinessApi by lazy {
        retrofit.create(BusinessApi::class.java)
    }
}
