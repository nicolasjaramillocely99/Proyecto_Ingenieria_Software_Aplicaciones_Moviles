package com.example.vinylsapp.di

import com.example.vinylsapp.data.network.AlbumApiService
import com.example.vinylsapp.data.network.MusicianApiService
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
    
    // URL base del backend
    // Backend hosted en Render: https://backvynils-8c16.onrender.com/
    // Para backend local en Docker (emulador): http://10.0.2.2:3000/
    // Para backend local en dispositivo físico: http://<IP_DE_TU_PC>:3000/
    private const val BASE_URL = "https://backvynils-8c16.onrender.com/"
    
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

    @Provides
    @Singleton
    fun provideArtistApiService(retrofit: Retrofit): MusicianApiService {
        return retrofit.create(MusicianApiService::class.java)
    }
}
