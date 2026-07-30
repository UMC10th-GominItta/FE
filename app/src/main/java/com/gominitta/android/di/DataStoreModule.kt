package com.gominitta.android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.gominittaDataStore: DataStore<Preferences> by preferencesDataStore(name = "gominitta_prefs")

/**
 * 로컬 전용(API 연동 전) 영속 저장소. 기능별로 Preferences 키만 나눠 쓰고,
 * DataStore 인스턴스 자체는 앱 전체가 공유한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.gominittaDataStore
}
