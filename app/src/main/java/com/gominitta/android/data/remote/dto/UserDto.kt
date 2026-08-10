package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val nickname: String? = null,
    val email: String? = null,
    val profileIcon: String? = null,
)

@Serializable
data class UserUpdateRequest(
    val nickname: String? = null,
    val profileIcon: String? = null,
)

@Serializable
data class UserUpdateResponse(
    val nickname: String? = null,
    val profileIcon: String? = null,
)

@Serializable
data class UserHomeResponse(
    val user: HomeUserDto? = null,
    val dailyMessage: DailyMessageDto? = null,
    val mindSession: MindSessionDto? = null,
)

@Serializable
data class HomeUserDto(
    val nickname: String? = null,
    val profileIcon: String? = null,
)

@Serializable
data class DailyMessageDto(
    val content: String? = null,
)

@Serializable
data class MindSessionDto(
    val sessionId: Long? = null,
    val title: String? = null,
    val status: String? = null,
    val startedAt: String? = null,
)