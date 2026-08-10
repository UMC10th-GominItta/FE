package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.HomeData
import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject

class GetHomeUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): HomeData = userRepository.getHome()
}
