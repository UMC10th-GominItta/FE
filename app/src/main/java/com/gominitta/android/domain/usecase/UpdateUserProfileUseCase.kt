package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(nickname: String?, profileImageUrl: String?) {
        if (!nickname.isNullOrBlank()) repository.updateNickname(nickname)
        if (!profileImageUrl.isNullOrBlank()) repository.updateProfileImage(profileImageUrl)
    }
}