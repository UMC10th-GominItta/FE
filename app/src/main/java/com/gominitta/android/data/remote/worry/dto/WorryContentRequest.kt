package com.gominitta.android.data.remote.worry.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorryContentRequest(
    val content: String,
)
