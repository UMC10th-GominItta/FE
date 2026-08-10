package com.gominitta.android.data.remote.worry

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.worry.dto.WorryCreateRequest
import com.gominitta.android.data.remote.worry.dto.WorryCreateResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WorryApi {
    @POST("api/v1/worries")
    suspend fun createWorry(@Body req: WorryCreateRequest): ApiResponse<WorryCreateResponse>
}
