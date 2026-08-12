package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnxietyGapResponse(
    val period: String,
    val beforeScore: Double,
    val afterScore: Double,
    val gap: Double,
    val sampleCount: Long,
    val feedback: String,
)
