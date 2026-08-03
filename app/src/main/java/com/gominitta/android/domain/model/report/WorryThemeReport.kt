package com.gominitta.android.domain.model.report

data class WorryThemeReport(
    val period: String,
    val topCategory: String?,
    val themes: List<WorryThemeCount>,
    val feedback: String,
)

data class WorryThemeCount(
    val category: String,
    val count: Int,
)

data class AnxietyGapReport(
    val period: String,
    val beforeScore: Double,
    val afterScore: Double,
    val gap: Double,
    val sampleCount: Int,
    val feedback: String,
)
