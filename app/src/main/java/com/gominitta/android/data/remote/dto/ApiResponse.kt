package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Server-wide response envelope. Every endpoint returns this shape, success or failure:
 * `{ "success": bool, "code": string, "message": string, "data": T? }`.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T? = null,
)