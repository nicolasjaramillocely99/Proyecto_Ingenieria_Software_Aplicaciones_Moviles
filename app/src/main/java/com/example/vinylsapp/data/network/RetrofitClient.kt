package com.example.vinylsapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Objeto singleton para configurar Retrofit
 * Configura el cliente HTTP y el conversor JSON
 */
object RetrofitClient {
    
    // URL base del backend - Cambia esto según tu configuración
    // Si usas emulador de Android: http://10.0.2.2:3000/
    // Si usas dispositivo físico en la misma red: http://<IP_DE_TU_PC>:3000/
    private const val BASE_URL = "http://10.0.2.2:3000/"
    
    /**
     * Interceptor para logging de peticiones HTTP (útil para debugging)
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    /**
     * Cliente HTTP configurado con timeouts y logging
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Instancia de Retrofit configurada con:
     * - URL base del backend
     * - Conversor Gson para JSON
     * - Cliente HTTP personalizado
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Instancia del servicio de API de álbumes
     */
    val albumApiService: AlbumApiService by lazy {
        retrofit.create(AlbumApiService::class.java)
    }
}
