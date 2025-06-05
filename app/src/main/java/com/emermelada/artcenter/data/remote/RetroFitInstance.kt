package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.utils.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Object que provee instancias configuradas de Retrofit para comunicarse con la API de ArtCenter.
 *
 * Incluye clientes HTTP con y sin autenticación, así como referencias a los servicios de autenticación
 * y de operaciones generales de ArtCenter.
 */
object RetroFitInstance {

    /**
     * URL base para todas las peticiones a la API de ArtCenter.
     */
    private const val BASE_URL = "http://192.168.1.65:5000/api/"

    /**
     * Cliente HTTP de OkHttp configurado sin cabeceras de autenticación.
     *
     * Se utiliza para las llamadas que no requieren token de acceso, como login o registro.
     * - Tiempo de conexión máximo: 10 segundos.
     * - Tiempo de lectura máximo: 30 segundos.
     * - Tiempo de escritura máximo: 15 segundos.
     */
    private val clientWithoutAuth = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Cliente HTTP de OkHttp configurado con un interceptor que añade el token de autenticación (Bearer).
     *
     * Se utiliza para las llamadas que requieren que el usuario esté autenticado:
     * - Obtiene el token desde [SessionManager.bearerToken].
     * - Si el token no es nulo ni vacío, lo añade en la cabecera "Authorization".
     * - Tiempo de conexión máximo: 10 segundos.
     * - Tiempo de lectura máximo: 30 segundos.
     * - Tiempo de escritura máximo: 15 segundos.
     */
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
        }
        .build()

    /**
     * Servicio de autenticación de Retrofit para operaciones de login y registro.
     *
     * Se inicializa de manera perezosa (lazy) utilizando [clientWithoutAuth].
     */
    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithoutAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    /**
     * Servicio principal de Retrofit para operaciones generales de ArtCenter (categorías, publicaciones, comentarios, etc.).
     *
     * Se inicializa de manera perezosa (lazy) utilizando [clientWithAuth] para incluir el token de autenticación en las peticiones.
     */
    val api: ArtCenterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArtCenterApiService::class.java)
    }
}
