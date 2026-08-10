package com.gominitta.android.di

import com.gominitta.android.data.repository.FakeSampleRepository
import com.gominitta.android.data.repository.RealRecipeRepository
import com.gominitta.android.data.repository.FavoriteTimeRepositoryImpl
import com.gominitta.android.data.repository.ReportRepositoryImpl
import com.gominitta.android.data.repository.DummyFavoriteTimeRepository
import com.gominitta.android.data.repository.WorryRepositoryImpl
import com.gominitta.android.domain.repository.RecipeRepository
import com.gominitta.android.domain.repository.SampleRepository
import com.gominitta.android.domain.repository.ReportRepository
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import com.gominitta.android.domain.repository.WorryRepository
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

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        repository: ReportRepositoryImpl,
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteTimeRepository(
        impl: FavoriteTimeRepositoryImpl,
    ): FavoriteTimeRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        real: RealRecipeRepository,
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindWorryRepository(
        impl: WorryRepositoryImpl,
    ): WorryRepository
}
