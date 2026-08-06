package com.gominitta.android.di

import com.gominitta.android.data.auth.DataStoreTokenStore
import com.gominitta.android.data.auth.DefaultKakaoLoginClient
import com.gominitta.android.data.auth.KakaoLoginClient
import com.gominitta.android.data.auth.TokenStore
import com.gominitta.android.data.repository.AuthRepositoryImpl
import com.gominitta.android.data.repository.UserRepositoryImpl
import com.gominitta.android.domain.repository.AuthRepository
import com.gominitta.android.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindKakaoLoginClient(impl: DefaultKakaoLoginClient): KakaoLoginClient

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindTokenStore(impl: DataStoreTokenStore): TokenStore

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
