package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.Greeting
import com.gominitta.android.domain.repository.SampleRepository
import javax.inject.Inject

/**
 * Domain use case — one unit of application logic.
 *
 * Invoked as a function: `getGreeting()`. ViewModels depend on use cases,
 * not on repositories directly. Constructor-injected by Hilt (no module needed).
 */
class GetGreetingUseCase @Inject constructor(
    private val repository: SampleRepository,
) {
    suspend operator fun invoke(): Greeting = repository.getGreeting()
}
