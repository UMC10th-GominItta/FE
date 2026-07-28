package com.gominitta.android.di

import com.gominitta.android.data.auth.DefaultKakaoLoginClient
import com.gominitta.android.data.auth.KakaoLoginClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindKakaoLoginClient(impl: DefaultKakaoLoginClient): KakaoLoginClient
}
