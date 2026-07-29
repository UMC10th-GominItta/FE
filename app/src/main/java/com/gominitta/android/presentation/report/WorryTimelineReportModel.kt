package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** 백엔드가 상대 최댓값을 기준으로 계산한 Level 0~4 히트맵 응답 계약입니다. */
data class WorryTimelineReportData(
    val totalCount: Int,
    val levels: List<List<Int>>,
) {
    val canRender: Boolean get() = totalCount >= MINIMUM_WORRY_COUNT

    companion object {
        const val MINIMUM_WORRY_COUNT = 5
    }
}

internal data class TimelineTimeSlot(
    val label: String,
    val range: String,
)

internal val timelineTimeSlots = listOf(
    TimelineTimeSlot("아침", "06-12시"),
    TimelineTimeSlot("오후", "12-18시"),
    TimelineTimeSlot("저녁", "18-24시"),
    TimelineTimeSlot("밤", "00-06시"),
)

private val dayLabels = listOf(
    "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일",
)

internal fun WorryTimelineReportData.feedbackText(): String {
    val peaks = levels.flatMapIndexed { timeIndex, row ->
        row.mapIndexedNotNull { dayIndex, level ->
            if (level == 4 && timeIndex in timelineTimeSlots.indices && dayIndex in dayLabels.indices) {
                "${dayLabels[dayIndex]} ${timelineTimeSlots[timeIndex].label} " +
                    "시간대(${timelineTimeSlots[timeIndex].range})"
            } else {
                null
            }
        }
    }.take(2)

    return when (peaks.size) {
        0 -> ""
        1 -> "${peaks.first()}에\n걱정 기록이 많았어요."
        else -> "${peaks[0]}와\n${peaks[1]}에\n걱정 기록이 많았어요."
    }
}

internal const val TIMELINE_TIP =
    "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요."

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
