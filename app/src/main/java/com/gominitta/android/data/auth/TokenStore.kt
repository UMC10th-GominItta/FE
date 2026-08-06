package com.gominitta.android.data.auth

/** 로그인 토큰 저장소 seam. 실제 구현은 [DataStoreTokenStore]. */
interface TokenStore {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}
