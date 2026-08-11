package com.gominitta.android.di

import com.gominitta.android.data.remote.api.SessionApi
import com.gominitta.android.data.repository.FakeSessionRepository
import com.gominitta.android.data.repository.SessionRepositoryImpl
import com.gominitta.android.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * 마음 세션 DI.
 *
 * 서버가 준비되기 전에는 [bindSessionRepository] 의 대상을 [FakeSessionRepository] 로,
 * 연동 후에는 [SessionRepositoryImpl] 로 바꾼다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
    // 서버 연동 전으로 되돌리려면 위 줄을 주석 처리하고 아래를 사용:
    // abstract fun bindSessionRepository(fake: FakeSessionRepository): SessionRepository

    companion object {
        @Provides
        @Singleton
        fun provideSessionApi(retrofit: Retrofit): SessionApi =
            retrofit.create(SessionApi::class.java)
    }
}
