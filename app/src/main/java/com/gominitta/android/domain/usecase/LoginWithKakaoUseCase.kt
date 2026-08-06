package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.AuthResult
import com.gominitta.android.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(kakaoAccessToken: String): AuthResult =
        authRepository.loginWithKakao(kakaoAccessToken)
}
