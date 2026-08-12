package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorryThemeResponse(
    val hasEnoughData: Boolean,
    val topTheme: String,
    val totalCount: Long,
    val themes: List<WorryThemeCountResponse> = emptyList(),
)

@Serializable
data class WorryThemeCountResponse(
    val theme: String,
    val count: Long,
)
