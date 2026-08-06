package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteTimeRequest(
    val label: String,
    val start_time: String,
    val end_time: String,
)

@Serializable
data class FavoriteTimeResponse(
    val favoriteTimeId: Long,
)

@Serializable
data class FavoriteTimeDetailResponse(
    val id: Long,
    val label: String,
    val start_time: String,
    val end_time: String,
)