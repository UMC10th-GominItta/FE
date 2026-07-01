package com.gominitta.android.data.repository

import javax.inject.Inject

/**
 * In-memory fake implementation of [SampleRepository].
 *
 * Used during development and tests. Swap for a real implementation
 * by rebinding in [com.gominitta.android.di.AppModule].
 */
class FakeSampleRepository @Inject constructor() : SampleRepository {
    override suspend fun getGreeting(): String = "Hello from Gominitta scaffold!"
}
