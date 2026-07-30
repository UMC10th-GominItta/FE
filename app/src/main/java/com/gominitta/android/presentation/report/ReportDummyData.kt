package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** API 연결 전 기간별 리포트 화면을 확인하기 위한 더미 데이터입니다. */
internal fun anxietyDummyData(range: DateRangeOption): AnxietyReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> AnxietyReportData(6, 8.0, 4.0)
    DateRangeOption.LAST_2_WEEKS -> AnxietyReportData(3, 4.0, 8.0)
    DateRangeOption.LAST_60_DAYS -> AnxietyReportData(12, 6.0, 6.0)
}

/** API 연결 전 기간 필터와 8개 걱정 테마 노출을 확인하기 위한 더미 데이터입니다. */
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData {
    val percentages = when (range) {
        DateRangeOption.LAST_2_WEEKS -> listOf(29, 21, 17, 13, 8, 6, 4, 2)
        DateRangeOption.LAST_30_DAYS -> listOf(32, 20, 16, 12, 8, 5, 4, 3)
        DateRangeOption.LAST_60_DAYS -> listOf(30, 22, 16, 11, 8, 6, 4, 3)
    }
    return WorryThemeReportData(
        totalCount = 100,
        themes = WorryTheme.entries.mapIndexed { index, theme ->
            WorryThemeItem(theme, percentages[index])
        },
    )
}

/** API 연결 전 요일·시간대별 히트맵을 확인하기 위한 더미 데이터입니다. */
internal fun worryTimelineDummyData(range: DateRangeOption): WorryTimelineReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> WorryTimelineReportData(
        totalCount = 20,
        levels = listOf(
            listOf(0, 1, 2, 1, 2, 1, 0),
            listOf(1, 2, 2, 3, 2, 1, 1),
            listOf(1, 2, 3, 4, 3, 2, 1),
            listOf(0, 1, 2, 3, 2, 2, 4),
        ),
    )
    DateRangeOption.LAST_2_WEEKS -> WorryTimelineReportData(
        totalCount = 10,
        levels = listOf(
            listOf(0, 1, 1, 2, 2, 1, 0),
            listOf(1, 2, 2, 3, 4, 2, 1),
            listOf(1, 2, 3, 3, 4, 2, 1),
            listOf(0, 1, 2, 2, 3, 1, 1),
        ),
    )
    DateRangeOption.LAST_60_DAYS -> WorryTimelineReportData(
        totalCount = 40,
        levels = listOf(
            listOf(1, 2, 2, 3, 3, 2, 2),
            listOf(2, 2, 3, 3, 3, 2, 2),
            listOf(2, 3, 3, 4, 4, 3, 2),
            listOf(1, 2, 3, 3, 3, 2, 2),
        ),
    )
}
