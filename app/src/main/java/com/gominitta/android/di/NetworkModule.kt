package com.gominitta.android.di

import com.gominitta.android.BuildConfig
import com.gominitta.android.data.remote.api.ReportApi
import com.gominitta.android.data.remote.AuthInterceptor
import com.gominitta.android.data.remote.TokenAuthenticator
import com.gominitta.android.data.remote.worry.WorryApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Provides the shared Retrofit/OkHttp stack. Feature modules provide their own
 * `@Provides fun provideXxxApi(retrofit: Retrofit): XxxApi` off of [provideRetrofit].
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://www.gominitta.cloud/"

    /** 서버 STT/OCR 추론까지 기다려야 해서 OkHttp 기본값(10초)으론 모자란다. */
    private const val MEDIA_UPLOAD_TIMEOUT_SECONDS = 60

    private val Request.isMediaUpload: Boolean
        get() = url.encodedPath.endsWith("/records/voice") ||
            url.encodedPath.endsWith("/records/handwriting")

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            // 음성/필기 업로드만 여유를 준다 — 전체를 늘리면 평범한 요청 실패도 그만큼 매달린다.
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.isMediaUpload) {
                    chain
                        .withReadTimeout(MEDIA_UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .withWriteTimeout(MEDIA_UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .proceed(request)
                } else {
                    chain.proceed(request)
                }
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .authenticator(tokenAuthenticator)
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

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit): ReportApi = retrofit.create(ReportApi::class.java)

    @Provides
    @Singleton
    fun provideWorryApi(retrofit: Retrofit): WorryApi = retrofit.create(WorryApi::class.java)
}
