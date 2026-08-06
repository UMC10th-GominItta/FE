package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.UserProfileResponse
import retrofit2.http.GET

interface UsersApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): ApiResponse<UserProfileResponse>
}
