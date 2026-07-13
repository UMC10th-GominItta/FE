package com.gominitta.android.di

import com.gominitta.android.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Provides the shared Retrofit/OkHttp stack. Feature modules provide their own
 * `@Provides fun provideXxxApi(retrofit: Retrofit): XxxApi` off of [provideRetrofit]
 * (see e.g. SessionNetworkModule).
 *
 * TODO: once the login flow lands, add an auth interceptor here that attaches
 * `Authorization: Bearer {Access_Token}` from wherever the token ends up stored.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // TODO: 실제 서버 base URL로 교체
    private const val BASE_URL = "https://api.gominitta.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}