package com.gominitta.android.data.remote

import com.gominitta.android.data.remote.dto.ApiResponse
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

private val errorEnvelopeJson = Json { ignoreUnknownKeys = true }

/**
 * Outcome of a [safeApiCall] / [safeApiCallUnit]. Success/failure is a value, not a
 * thrown exception, so callers are forced to branch with `when` instead of relying on
 * try/catch.
 */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val code: String, val message: String) : ApiResult<Nothing>
    data class NetworkError(val cause: Throwable) : ApiResult<Nothing>
}

/**
 * Runs a Retrofit call and unwraps the [ApiResponse] envelope into an [ApiResult].
 *
 * Use this for endpoints whose success response always carries a `data` payload
 * (e.g. login returning tokens/profile). If `success: true` but `data` is missing,
 * that's treated as [ApiResult.Error] — the server broke its own contract.
 *
 * For endpoints that succeed *without* a payload (logout, delete, ...), use
 * [safeApiCallUnit] instead — do NOT call this one with `T = Unit`, since a missing
 * `data` would then incorrectly surface as an error.
 */
suspend fun <T : Any> safeApiCall(apiCall: suspend () -> ApiResponse<T>): ApiResult<T> = runApiCatching {
    val response = apiCall()
    if (!response.success) return@runApiCatching ApiResult.Error(response.code, response.message)
    response.data?.let { ApiResult.Success(it) } ?: ApiResult.Error(response.code, response.message)
}

/**
 * Same as [safeApiCall], but for endpoints that succeed without returning a `data`
 * payload (logout, delete, ...). `data` is ignored — a `success: true` response is
 * always [ApiResult.Success] regardless of whether `data` is present.
 */
suspend fun safeApiCallUnit(apiCall: suspend () -> ApiResponse<Unit?>): ApiResult<Unit> = runApiCatching {
    val response = apiCall()
    if (response.success) ApiResult.Success(Unit) else ApiResult.Error(response.code, response.message)
}

private suspend fun <T> runApiCatching(block: suspend () -> ApiResult<T>): ApiResult<T> = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    e.toApiError()
} catch (e: IOException) {
    ApiResult.NetworkError(e)
} catch (e: SerializationException) {
    ApiResult.Error(code = "PARSE_ERROR", message = "서버 응답을 처리할 수 없습니다.")
}

private fun HttpException.toApiError(): ApiResult.Error {
    val envelope = response()?.errorBody()?.string()?.let { body ->
        runCatching { errorEnvelopeJson.decodeFromString<ApiResponse<Unit>>(body) }.getOrNull()
    }
    return ApiResult.Error(
        code = envelope?.code ?: "HTTP_${code()}",
        message = envelope?.message ?: "요청 처리 중 오류가 발생했습니다.",
    )
}
