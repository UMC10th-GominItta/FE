package com.gominitta.android.di

import com.gominitta.android.data.remote.api.RecipeApi
import com.gominitta.android.data.remote.api.RecipeLogApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RecipeNetworkModule {

    @Provides
    @Singleton
    fun provideRecipeApi(retrofit: Retrofit): RecipeApi = retrofit.create(RecipeApi::class.java)

    @Provides
    @Singleton
    fun provideRecipeLogApi(retrofit: Retrofit): RecipeLogApi = retrofit.create(RecipeLogApi::class.java)
}