package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.Greeting
import com.gominitta.android.domain.repository.SampleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [FakeSampleRepository] against the domain [SampleRepository]
 * contract. Swap the subject to any real implementation and these must pass.
 */
class FakeSampleRepositoryTest {

    private val repository: SampleRepository = FakeSampleRepository()

    @Test
    fun `getGreeting returns non-blank message`() = runTest {
        val result: Greeting = repository.getGreeting()
        assert(result.message.isNotBlank()) { "Expected non-blank greeting, got: '${result.message}'" }
    }

    @Test
    fun `getGreeting returns expected scaffold message`() = runTest {
        val result = repository.getGreeting()
        assertEquals("Hello from Gominitta scaffold!", result.message)
    }

    @Test
    fun `getGreeting is idempotent across multiple calls`() = runTest {
        val first = repository.getGreeting()
        val second = repository.getGreeting()
        assertEquals(first, second)
    }
}
