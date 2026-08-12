package com.gominitta.android.domain.model.report

data class WorryThemeReport(
    val period: String,
    val topCategory: String?,
    val themes: List<WorryThemeCount>,
    val feedback: String,
)

data class WorryThemeCount(
    val category: String,
    val count: Long,
)

data class AnxietyGapReport(
    val period: String,
    val hasEnoughData: Boolean,
    val avgBefore: Int,
    val avgAfter: Int,
    val gap: Int,
    val improved: Boolean,
)

data class WorryTimelineReport(
    val period: String,
    val cells: List<WorryTimelineCell>,
    val peaks: List<WorryTimelinePeak>,
    val feedback: String,
)

data class WorryTimelineCell(
    val dayOfWeek: ReportDayOfWeek,
    val timeSlot: ReportTimeSlot,
    val count: Long,
)

data class WorryTimelinePeak(
    val dayOfWeek: ReportDayOfWeek,
    val timeSlot: ReportTimeSlot,
)

enum class ReportDayOfWeek { MON, TUE, WED, THU, FRI, SAT, SUN }

enum class ReportTimeSlot { DAWN, MORNING, AFTERNOON, EVENING }
