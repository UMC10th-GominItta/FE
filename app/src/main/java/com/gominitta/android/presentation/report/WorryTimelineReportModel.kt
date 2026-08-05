package com.gominitta.android.presentation.report

/**
 * 선택한 기간의 걱정 기록을 요일과 시간대별 히트맵 단계로 요약한 화면 모델입니다.
 *
 * @property totalCount 선택한 기간에 작성된 전체 걱정 기록 수
 * @property levels 시간대 4행 × 요일 7열로 구성된 히트맵 단계 목록.
 * 각 값은 0~4이며, 행은 아침·오후·저녁·밤, 열은 월요일부터 일요일 순서입니다.
 */
data class WorryTimelineReportData(
    val totalCount: Long,
    val levels: List<List<Int>>,
    val feedback: String,
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

internal const val TIMELINE_TIP =
    "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요."

private const val MINIMUM_TIMELINE_COUNT = 5L
