package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.AuthTokenResponse
import com.gominitta.android.data.remote.dto.KakaoLoginRequest
import com.gominitta.android.data.remote.dto.RefreshRequest
import com.gominitta.android.data.remote.dto.LogoutRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/kakao")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): ApiResponse<AuthTokenResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): ApiResponse<AuthTokenResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): ApiResponse<Unit?>
}
