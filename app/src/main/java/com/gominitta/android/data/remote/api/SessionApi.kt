package com.gominitta.android.data.remote.api

import com.gominitta.android.data.remote.dto.ApiResponse
import com.gominitta.android.data.remote.dto.RecordUpdateRequestDto
import com.gominitta.android.data.remote.dto.SessionDetailResponseDto
import com.gominitta.android.data.remote.dto.SessionListResponseDto
import com.gominitta.android.data.remote.dto.SessionRecordResponseDto
import com.gominitta.android.data.remote.dto.SessionStatusChangeRequestDto
import com.gominitta.android.data.remote.dto.SessionStatusChangeResponseDto
import com.gominitta.android.data.remote.dto.TextRecordCreateRequestDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/** 마음 세션 · 세션 기록 엔드포인트. */
interface SessionApi {
    @GET("api/v1/sessions")
    suspend fun getSessions(
        @Query("status") status: String? = null,
    ): ApiResponse<List<SessionListResponseDto>>

    @GET("api/v1/sessions/{sessionId}")
    suspend fun getSessionDetail(
        @Path("sessionId") sessionId: Long,
    ): ApiResponse<SessionDetailResponseDto>

    /** 시작(in_progress) / 완료(completed) 공용. 완료 시 emotionScoreAfter 필수. */
    @PATCH("api/v1/sessions/{id}")
    suspend fun changeStatus(
        @Path("id") sessionId: Long,
        @Body body: SessionStatusChangeRequestDto,
    ): ApiResponse<SessionStatusChangeResponseDto>


    @GET("api/v1/sessions/{id}/records")
    suspend fun getRecords(
        @Path("id") sessionId: Long,
        @Query("recordType") recordType: String? = null,
    ): ApiResponse<List<SessionRecordResponseDto>>

    @POST("api/v1/sessions/{id}/records")
    suspend fun createTextRecord(
        @Path("id") sessionId: Long,
        @Body body: TextRecordCreateRequestDto,
    ): ApiResponse<SessionRecordResponseDto>

    @Multipart
    @POST("api/v1/sessions/{id}/records/voice")
    suspend fun createVoiceRecord(
        @Path("id") sessionId: Long,
        @Part file: MultipartBody.Part,
    ): ApiResponse<SessionRecordResponseDto>

    @Multipart
    @POST("api/v1/sessions/{id}/records/handwriting")
    suspend fun createHandwritingRecord(
        @Path("id") sessionId: Long,
        @Part file: MultipartBody.Part,
    ): ApiResponse<SessionRecordResponseDto>

    @PATCH("api/v1/sessions/{id}/records/{recordId}")
    suspend fun updateRecord(
        @Path("id") sessionId: Long,
        @Path("recordId") recordId: Long,
        @Body body: RecordUpdateRequestDto,
    ): ApiResponse<SessionRecordResponseDto>

    @DELETE("api/v1/sessions/{id}/records/{recordId}")
    suspend fun deleteRecord(
        @Path("id") sessionId: Long,
        @Path("recordId") recordId: Long,
    ): ApiResponse<Unit?>
}
