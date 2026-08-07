package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnxietyGapResponse(
    val period: String,
    val beforeScore: Long,
    val afterScore: Long,
    val gap: Long,
    val sampleCount: Long,
    val feedback: String,
)
