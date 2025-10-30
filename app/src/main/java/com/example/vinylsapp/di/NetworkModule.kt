package com.example.vinylsapp.di

import com.example.vinylsapp.data.network.AlbumApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo de Hilt para inyección de dependencias de red
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // URL base del backend Docker
    // El backend corre en Docker en el puerto 3000
    // Para emulador Android: http://10.0.2.2:3000/ (10.0.2.2 = localhost del host)
    // Para dispositivo físico: http://<IP_DE_TU_PC>:3000/ (reemplaza con tu IP local)
    // Ejemplo IP local: http://192.168.1.100:3000/
    private const val BASE_URL = "http://10.0.2.2:3000/"
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAlbumApiService(retrofit: Retrofit): AlbumApiService {
        return retrofit.create(AlbumApiService::class.java)
    }
}
