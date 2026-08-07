package com.gominitta.android.di

import com.gominitta.android.data.remote.api.FavoriteTimeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object MyPageNetworkModule {

    @Provides
    @Singleton
    fun provideFavoriteTimeApi(retrofit: Retrofit): FavoriteTimeApi =
        retrofit.create(FavoriteTimeApi::class.java)
}