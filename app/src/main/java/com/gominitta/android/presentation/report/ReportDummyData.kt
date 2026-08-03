package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** API 연결 전 기간별 리포트 화면을 확인하기 위한 더미 데이터입니다. */
internal fun anxietyDummyData(range: DateRangeOption): AnxietyReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> AnxietyReportData("30d", 8.0, 4.0, -4.0, 6, "걱정을 마주하고 마음이 한결 가벼워졌어요.")
    DateRangeOption.LAST_2_WEEKS -> AnxietyReportData("2w", 4.0, 8.0, 4.0, 3, "아직은 마음에 복잡한 생각들이 남아있네요.")
    DateRangeOption.LAST_60_DAYS -> AnxietyReportData("60d", 6.0, 6.0, 0.0, 12, "불안 점수가 비슷하게 유지되었어요.")
}

/** API 연결 전 기간 필터와 8개 걱정 테마 노출을 확인하기 위한 더미 데이터입니다. */
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData {
    val counts = when (range) {
        DateRangeOption.LAST_2_WEEKS -> listOf(29, 21, 17, 13, 8, 6, 4, 2)
        DateRangeOption.LAST_30_DAYS -> listOf(32, 20, 16, 12, 8, 5, 4, 3)
        DateRangeOption.LAST_60_DAYS -> listOf(30, 22, 16, 11, 8, 6, 4, 3)
    }
    return WorryThemeReportData(
        period = range.apiValue,
        topCategory = WorryTheme.CAREER,
        themes = WorryTheme.entries.mapIndexed { index, theme ->
            WorryThemeItem(theme, counts[index])
        },
        feedback = "최근에는 진로와 관련된 걱정이 가장 많았어요.",
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
