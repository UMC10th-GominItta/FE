package com.gominitta.android.data.repository

import com.gominitta.android.data.auth.TokenStore
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.AuthApi
import com.gominitta.android.data.remote.dto.KakaoLoginRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.AuthResult
import com.gominitta.android.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) : AuthRepository {

    override suspend fun loginWithKakao(kakaoAccessToken: String): AuthResult {
        val result = safeApiCall { authApi.loginWithKakao(KakaoLoginRequest(kakaoAccessToken)) }
        val data = when (result) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> throw IllegalStateException(result.message)
            is ApiResult.NetworkError -> throw result.cause
        }
        tokenStore.saveTokens(data.accessToken, data.refreshToken)
        return AuthResult(isNewUser = data.isNewUser)
    }
}
