package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.Greeting
import com.gominitta.android.domain.repository.SampleRepository
import javax.inject.Inject

/**
 * In-memory fake implementation of the domain [SampleRepository].
 *
 * Used during development and tests. Swap for a real data-source-backed
 * implementation by rebinding in [com.gominitta.android.di.AppModule].
 */
class FakeSampleRepository @Inject constructor() : SampleRepository {
    override suspend fun getGreeting(): Greeting =
        Greeting("Hello from Gominitta scaffold!")
}
