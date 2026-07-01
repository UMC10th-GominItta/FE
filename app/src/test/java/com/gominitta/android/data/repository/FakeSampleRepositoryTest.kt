package com.gominitta.android.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for [FakeSampleRepository].
 *
 * These tests intentionally target the [SampleRepository] interface, not the
 * concrete class. Swap the subject under test to any real implementation and
 * all assertions must still pass — that is the definition of "swappable".
 *
 * When a real implementation is added:
 *   1. Create `RealSampleRepositoryTest` targeting the same interface contract.
 *   2. Reuse the same assertion helpers if useful.
 *   3. No changes are needed here.
 */
class FakeSampleRepositoryTest {

    // Subject typed as the interface — proves swappability.
    private val repository: SampleRepository = FakeSampleRepository()

    @Test
    fun `getGreeting returns non-null non-blank string`() = runTest {
        val result = repository.getGreeting()
        assertNotNull(result)
        assert(result.isNotBlank()) { "Expected non-blank greeting, got: '$result'" }
    }

    @Test
    fun `getGreeting returns expected scaffold message`() = runTest {
        val result = repository.getGreeting()
        assertEquals("Hello from Gominitta scaffold!", result)
    }

    @Test
    fun `getGreeting is idempotent across multiple calls`() = runTest {
        val first  = repository.getGreeting()
        val second = repository.getGreeting()
        assertEquals(first, second)
    }
}
