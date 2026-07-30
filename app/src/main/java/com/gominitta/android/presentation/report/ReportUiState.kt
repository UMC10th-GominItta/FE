package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/**
 * 마음 리포트 화면 전체의 상태입니다.
 *
 * 각 리포트 카드는 서로 다른 조회 기간을 선택할 수 있으므로 기간과 데이터를
 * 카드별로 독립적으로 보관합니다.
 */
data class ReportUiState(
    val worryThemeRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    val worryThemeData: WorryThemeReportData? = null,
    val anxietyRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    val anxietyData: AnxietyReportData? = null,
    val timelineRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    val timelineData: WorryTimelineReportData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
