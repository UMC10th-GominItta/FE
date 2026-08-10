package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.AuthResult

interface AuthRepository {
    suspend fun loginWithKakao(kakaoAccessToken: String): AuthResult
    suspend fun logout()
}