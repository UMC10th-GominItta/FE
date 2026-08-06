package com.gominitta.android.data.remote

import com.gominitta.android.data.auth.TokenStore
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/** Attaches `Authorization: Bearer {accessToken}` to outgoing requests when a token exists. */
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.contains("/api/v1/auth/")) {
            return chain.proceed(original)
        }
        val token = runBlocking { tokenStore.getAccessToken() }
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
