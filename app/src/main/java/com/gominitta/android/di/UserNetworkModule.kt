package com.gominitta.android.di

import com.gominitta.android.data.remote.api.UsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * Provides feature-scoped Retrofit APIs off of [NetworkModule.provideRetrofit].
 */
@Module
@InstallIn(SingletonComponent::class)
object UserNetworkModule {

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UsersApi = retrofit.create(UsersApi::class.java)
}
