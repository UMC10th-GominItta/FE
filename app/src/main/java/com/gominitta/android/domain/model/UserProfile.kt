package com.gominitta.android.domain.model

data class UserProfile(
    val nickname: String,
    val profileImageUrl: String = "",
    val email: String = "",
)