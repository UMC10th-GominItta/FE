package com.gominitta.android.di

import com.gominitta.android.data.repository.DummyRecipeRepository
import com.gominitta.android.data.repository.FakeSampleRepository
import com.gominitta.android.data.repository.FakeSessionRepository
import com.gominitta.android.data.repository.ReportRepositoryImpl
import com.gominitta.android.data.repository.DummyFavoriteTimeRepository
import com.gominitta.android.domain.repository.RecipeRepository
import com.gominitta.android.domain.repository.SampleRepository
import com.gominitta.android.domain.repository.SessionRepository
import com.gominitta.android.domain.repository.ReportRepository
import com.gominitta.android.domain.repository.UserRepository
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-scoped Hilt module.
 *
 * Binds repository interfaces to their current implementations.
 * To swap to a real implementation:
 *   1. Create RealSampleRepository implementing SampleRepository.
 *   2. Change the @Binds target below.
 *   3. No changes needed in domain or presentation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSampleRepository(
        fake: FakeSampleRepository,
    ): SampleRepository

    // TODO: 실제 서버(NetworkModule.BASE_URL) 준비되면 SessionRepositoryImpl 로 교체
    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        fake: FakeSessionRepository,
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        repository: ReportRepositoryImpl,
    ): ReportRepository


    @Binds
    @Singleton
    abstract fun bindFavoriteTimeRepository(
        dummy: DummyFavoriteTimeRepository,
    ): FavoriteTimeRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        dummy: DummyRecipeRepository,
    ): RecipeRepository
}