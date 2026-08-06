package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val nickname: String? = null,
    val email: String? = null,
    val profileIcon: String? = null,
)
