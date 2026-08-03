package com.gominitta.android.presentation.report

/**
 * 선택한 기간의 걱정 기록을 요일과 시간대별 히트맵 단계로 요약한 화면 모델입니다.
 *
 * @property totalCount 선택한 기간에 작성된 전체 걱정 기록 수
 * @property levels 시간대 4행 × 요일 7열로 구성된 히트맵 단계 목록.
 * 각 값은 0~4이며, 행은 아침·오후·저녁·밤, 열은 월요일부터 일요일 순서입니다.
 */
data class WorryTimelineReportData(
    val totalCount: Int,
    val levels: List<List<Int>>,
) {
    /** 걱정 타임라인을 표시하기에 전체 걱정 기록 수가 충분한지 여부 */
    val canRender: Boolean get() = totalCount >= MINIMUM_TIMELINE_COUNT
}

/**
 * 타임라인의 한 행을 나타내는 시간대 정보입니다.
 *
 * @property label 사용자에게 보여주는 시간대 이름
 * @property range 해당 시간대에 포함되는 시간 범위 안내 문구
 */
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

private const val MINIMUM_TIMELINE_COUNT = 5
