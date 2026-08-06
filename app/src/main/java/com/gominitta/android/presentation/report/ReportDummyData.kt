package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** Compose Preview에서 기간별 리포트 화면을 확인하기 위한 고정 데이터입니다. */
internal fun anxietyDummyData(range: DateRangeOption): AnxietyReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> AnxietyReportData("30d", 8.0, 4.0, -4.0, 6, "걱정을 마주하고 마음이 한결 가벼워졌어요.")
    DateRangeOption.LAST_2_WEEKS -> AnxietyReportData("2w", 4.0, 8.0, 4.0, 3, "아직은 마음에 복잡한 생각들이 남아있네요.")
    DateRangeOption.LAST_60_DAYS -> AnxietyReportData("60d", 6.0, 6.0, 0.0, 12, "불안 점수가 비슷하게 유지되었어요.")
}

/** Compose Preview에서 걱정 테마 버블 배치를 확인하기 위한 고정 데이터입니다. */
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
            WorryThemeItem(theme, counts[index].toLong())
        },
        feedback = "최근에는 진로와 관련된 걱정을 가장 많이 하셨어요.",
    )
}

/** Compose Preview에서 요일·시간대별 히트맵을 확인하기 위한 고정 데이터입니다. */
internal fun worryTimelineDummyData(range: DateRangeOption): WorryTimelineReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> WorryTimelineReportData(
        totalCount = 20,
        levels = listOf(
            listOf(0, 1, 2, 1, 2, 1, 0),
            listOf(1, 2, 2, 3, 2, 1, 1),
            listOf(1, 2, 3, 4, 3, 2, 1),
            listOf(0, 1, 2, 3, 2, 2, 4),
        ),
        feedback = "목요일 저녁 시간대(18-24시)와\n일요일 밤 시간대(00-06시)에\n걱정 기록이 많았어요.",
    )
    DateRangeOption.LAST_2_WEEKS -> WorryTimelineReportData(
        totalCount = 10,
        levels = listOf(
            listOf(0, 1, 1, 2, 2, 1, 0),
            listOf(1, 2, 2, 3, 4, 2, 1),
            listOf(1, 2, 3, 3, 4, 2, 1),
            listOf(0, 1, 2, 2, 3, 1, 1),
        ),
        feedback = "최근 2주 동안 걱정이 자주 찾아온 시간대를 확인해 보세요.",
    )
    DateRangeOption.LAST_60_DAYS -> WorryTimelineReportData(
        totalCount = 40,
        levels = listOf(
            listOf(1, 2, 2, 3, 3, 2, 2),
            listOf(2, 2, 3, 3, 3, 2, 2),
            listOf(2, 3, 3, 4, 4, 3, 2),
            listOf(1, 2, 3, 3, 3, 2, 2),
        ),
        feedback = "최근 60일 동안 걱정이 자주 찾아온 시간대를 확인해 보세요.",
    )
}
