package com.gominitta.android.data.repository

/**
 * Sample repository interface — illustrates the data-layer seam.
 *
 * All data operations flow through this interface. Feature code depends
 * only on this contract, never on a concrete implementation.
 *
 * Replace [FakeSampleRepository] with a real Retrofit/Room implementation
 * in a future sprint; no feature code changes required.
 */
interface SampleRepository {
    /** Returns a greeting string. Replace with a real domain model. */
    suspend fun getGreeting(): String
}
