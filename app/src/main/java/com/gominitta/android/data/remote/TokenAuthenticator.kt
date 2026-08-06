package com.gominitta.android.data.remote

import com.gominitta.android.data.auth.SessionManager
import com.gominitta.android.data.auth.TokenStore
import com.gominitta.android.data.remote.api.AuthApi
import com.gominitta.android.data.remote.dto.RefreshRequest
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * 401 응답 시 refreshToken으로 accessToken을 갱신하고 원 요청을 재시도한다.
 * 갱신 실패 시 토큰을 지우고 [SessionManager]로 세션 만료를 알린다.
 */
class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApiProvider: Provider<AuthApi>,
    private val sessionManager: SessionManager,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("/api/v1/auth/")) return null
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = runBlocking { tokenStore.getAccessToken() }
            if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
                // 다른 스레드가 이미 갱신을 마쳤다 — 새 토큰으로 원 요청만 재시도.
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = runBlocking { tokenStore.getRefreshToken() }
            if (refreshToken == null) {
                sessionManager.notifySessionExpired()
                return null
            }

            val result = runCatching {
                runBlocking { authApiProvider.get().refresh(RefreshRequest(refreshToken)) }
            }
            val apiResponse = result.getOrNull()
            val data = apiResponse?.takeIf { it.success }?.data
            if (data == null) {
                runBlocking { tokenStore.clear() }
                sessionManager.notifySessionExpired()
                return null
            }

            runBlocking { tokenStore.saveTokens(data.accessToken, data.refreshToken) }
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${data.accessToken}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
