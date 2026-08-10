package com.gominitta.android.data.remote.worry.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorryCreateRequest(
    val content: String,
    val emotionScoreBefore: Int,
    @SerialName("scheduled_start_at") val scheduledStartAt: String,
    @SerialName("scheduled_end_at") val scheduledEndAt: String,
)
