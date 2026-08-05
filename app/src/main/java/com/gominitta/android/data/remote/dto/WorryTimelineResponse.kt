package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorryTimelineResponse(
    val period: String,
    val cells: List<WorryTimelineCellResponse> = emptyList(),
    val peaks: List<WorryTimelinePeakResponse> = emptyList(),
    val feedback: String,
)

@Serializable
data class WorryTimelineCellResponse(
    val dayOfWeek: ReportDayOfWeekResponse,
    val timeSlot: ReportTimeSlotResponse,
    val count: Long,
)

@Serializable
data class WorryTimelinePeakResponse(
    val dayOfWeek: ReportDayOfWeekResponse,
    val timeSlot: ReportTimeSlotResponse,
)

@Serializable
enum class ReportDayOfWeekResponse { MON, TUE, WED, THU, FRI, SAT, SUN }

@Serializable
enum class ReportTimeSlotResponse { DAWN, MORNING, AFTERNOON, EVENING }
