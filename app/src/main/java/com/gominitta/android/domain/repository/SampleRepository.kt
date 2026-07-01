package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.Greeting

/**
 * Data-layer seam, declared in the DOMAIN layer (dependency inversion).
 *
 * The domain owns this contract; the data layer implements it. Presentation
 * depends only on the domain (via use cases), never on data implementations.
 *
 * Swap [com.gominitta.android.data.repository.FakeSampleRepository] for a real
 * Retrofit/Room implementation later — no domain or presentation changes needed.
 */
interface SampleRepository {
    suspend fun getGreeting(): Greeting
}
