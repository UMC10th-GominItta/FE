package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption
import kotlin.random.Random

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
            WorryThemeItem(theme, counts[index].toLong())
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

/** 실기기 UI 검증 시 API 오류를 대신해 표시할 모델 호환 랜덤 데이터입니다. */
internal fun randomWorryThemeDummyData(
    range: DateRangeOption,
    random: Random = Random.Default,
): WorryThemeReportData {
    val themes = WorryTheme.entries.map { theme ->
        WorryThemeItem(theme = theme, count = random.nextLong(from = 1L, until = 31L))
    }
    val topTheme = themes.maxBy { it.count }.theme

    return WorryThemeReportData(
        period = range.apiValue,
        topCategory = topTheme,
        themes = themes,
        feedback = "최근에는 ${topTheme.label}와 관련된 걱정이 가장 많았어요.",
    )
}

internal fun randomAnxietyDummyData(
    range: DateRangeOption,
    random: Random = Random.Default,
): AnxietyReportData {
    val beforeScore = random.nextInt(from = 0, until = 11).toDouble()
    val afterScore = random.nextInt(from = 0, until = 11).toDouble()
    val gap = afterScore - beforeScore
    val feedback = when {
        gap < 0 -> "걱정을 마주한 뒤 불안 점수가 낮아졌어요."
        gap > 0 -> "마음에 남은 걱정을 조금 더 천천히 살펴봐요."
        else -> "불안 점수가 비슷하게 유지되었어요."
    }

    return AnxietyReportData(
        period = range.apiValue,
        beforeScore = beforeScore,
        afterScore = afterScore,
        gap = gap,
        sampleCount = random.nextLong(from = 1L, until = 21L),
        feedback = feedback,
    )
}

internal fun randomWorryTimelineDummyData(
    range: DateRangeOption,
    random: Random = Random.Default,
): WorryTimelineReportData {
    // 실제 API처럼 원본 count를 먼저 만든 뒤, 화면에 전달할 때만 0~4 색상 단계로 제한합니다.
    val counts = List(4) { rowIndex ->
        List(7) { dayIndex ->
            when {
                rowIndex == 0 && dayIndex == 0 -> 4
                rowIndex == 0 && dayIndex == 1 -> 1
                else -> random.nextInt(from = 0, until = 16)
            }
        }
    }
    val levels = counts.map { row -> row.map { count -> count.coerceIn(0, 4) } }
    val dayLabels = listOf(
        "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일",
    )
    val timeSlotLabels = listOf(
        "아침 시간대(06-12시)",
        "오후 시간대(12-18시)",
        "저녁 시간대(18-24시)",
        "밤 시간대(00-06시)",
    )
    // 피드백은 색상 단계가 아니라 손실 없는 원본 count를 기준으로 최상위 두 구간을 선택합니다.
    val peakLabels = counts
        .flatMapIndexed { timeIndex, row ->
            row.mapIndexed { dayIndex, count ->
                Triple(count, dayIndex, timeIndex)
            }
        }
        .sortedByDescending { (count) -> count }
        .take(2)
        .map { (_, dayIndex, timeIndex) ->
            "${dayLabels[dayIndex]} ${timeSlotLabels[timeIndex]}"
        }
    val feedback = when (peakLabels.size) {
        0 -> ""
        1 -> "${peakLabels.first()}에\n걱정 기록이 많았어요."
        else -> "${peakLabels[0]}와\n${peakLabels[1]}에\n걱정 기록이 많았어요."
    }

    return WorryTimelineReportData(
        totalCount = counts.sumOf { row -> row.sum().toLong() },
        levels = levels,
        feedback = feedback,
    )
}
