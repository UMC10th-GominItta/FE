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