package com.gominitta.android.data.remote

import com.gominitta.android.data.remote.dto.ApiResponse
import java.io.IOException
import kotlinx.serialization.json.Json
import retrofit2.HttpException


private val errorEnvelopeJson = Json { ignoreUnknownKeys = true }

/**
 * Runs a Retrofit call and unwraps the [ApiResponse] envelope, throwing a single
 * consistent error type ([ApiException] / [NetworkException]) regardless of whether
 * the failure came back as `success: false` in a 2xx body or as an HTTP error status
 * carrying the same envelope in its error body.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> ApiResponse<T>): T {
    val response = try {
        apiCall()
    } catch (e: HttpException) {
        throw e.toApiException()
    } catch (e: IOException) {
        throw NetworkException()
    }

    if (!response.success) throw ApiException(response.code, response.message)
    return response.data ?: throw ApiException(response.code, response.message)
}

private fun HttpException.toApiException(): ApiException {
    val envelope = response()?.errorBody()?.string()?.let { body ->
        runCatching { errorEnvelopeJson.decodeFromString<ApiResponse<Unit>>(body) }.getOrNull()
    }
    return ApiException(
        code = envelope?.code ?: "HTTP_${code()}",
        message = envelope?.message ?: "요청 처리 중 오류가 발생했습니다.",
    )
}