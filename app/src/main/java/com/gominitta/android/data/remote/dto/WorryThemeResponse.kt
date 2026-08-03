package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorryThemeResponse(
    val period: String,
    val topCategory: String? = null,
    val themes: List<WorryThemeCountResponse> = emptyList(),
    val feedback: String,
)

@Serializable
data class WorryThemeCountResponse(
    val category: String,
    val count: Int,
)
