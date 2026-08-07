package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): UserProfile = repository.getMyProfile()
}