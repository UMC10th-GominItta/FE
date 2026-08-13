package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnxietyGapResponse(
    val hasEnoughData: Boolean,
    val avgBefore: Int,
    val avgAfter: Int,
    val gap: Int,
    val improved: Boolean,
)
