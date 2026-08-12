package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorryTimelineResponse(
    val hasEnoughData: Boolean,
    val cells: List<WorryTimelineCellResponse> = emptyList(),
    val topCells: List<WorryTimelineCellResponse> = emptyList(),
)

@Serializable
data class WorryTimelineCellResponse(
    val dayOfWeek: String,
    val timeSlot: String,
    val count: Long,
)
