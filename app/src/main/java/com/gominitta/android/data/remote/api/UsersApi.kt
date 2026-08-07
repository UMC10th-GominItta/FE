package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.UserProfileResponse
import com.gominitta.android.data.remote.dto.UserUpdateRequest
import com.gominitta.android.data.remote.dto.UserUpdateResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UsersApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): ApiResponse<UserProfileResponse>

    @PATCH("api/v1/users/me")
    suspend fun updateMyProfile(@Body request: UserUpdateRequest): ApiResponse<UserUpdateResponse>

    @DELETE("api/v1/users/me")
    suspend fun deleteMe(): ApiResponse<Unit?>
}