package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.utils.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroFitInstance {
    private const val BASE_URL = "http://192.168.0.177:5000/api/"
    private val clientWithoutAuth = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val clientWithAuth = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = SessionManager.bearerToken
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }.build()

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithoutAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    val api: ArtCenterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArtCenterApiService::class.java)
    }

}