package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): UserProfile = userRepository.getMyProfile()
}
