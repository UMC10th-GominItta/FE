package com.gominitta.android.data.repository

import com.gominitta.android.data.auth.TokenStore
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.AuthApi
import com.gominitta.android.data.remote.dto.KakaoLoginRequest
import com.gominitta.android.data.remote.dto.LogoutRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.data.remote.safeApiCallUnit
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
            is ApiResult.Error -> throw IllegalStateException("[${result.code}] ${result.message}")
            is ApiResult.NetworkError -> throw result.cause
        }
        tokenStore.saveTokens(data.accessToken, data.refreshToken)
        return AuthResult(isNewUser = data.isNewUser)
    }

    override suspend fun logout() {
        val refreshToken = tokenStore.getRefreshToken()
        if (refreshToken != null) {
            // 서버 로그아웃 실패해도(네트워크 문제 등) 로컬 토큰은 반드시 지운다 —
            // 사용자 입장에선 "로그아웃 버튼 눌렀는데 반응 없음"이 제일 나쁜 경험이라서.
            safeApiCallUnit { authApi.logout(LogoutRequest(refreshToken)) }
        }
        tokenStore.clear()
    }
}