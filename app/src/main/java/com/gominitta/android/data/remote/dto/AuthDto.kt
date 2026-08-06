package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class KakaoLoginRequest(
    val kakaoAccessToken: String,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
)
