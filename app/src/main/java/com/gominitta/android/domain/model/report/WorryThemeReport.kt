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
