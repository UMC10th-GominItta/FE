package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/**
 * 걱정 타임라인의 임시 모델입니다.
 * [frequencies]는 아침·오후·저녁·밤 4행과 월~일 7열로 구성됩니다.
 */
internal data class WorryTimelineReportData(
    val frequencies: List<List<Int>>,
    val summary: String,
    val tip: String,
)

// 빈도 값은 HeatMap에서 전체 최댓값을 기준으로 0~4단계 색상으로 변환됩니다.
// 상단 분석 문구는 기획 확정 전 임시 값이며 tip 문구는 모든 기간에 동일합니다.
internal fun worryTimelineDummyData(range: DateRangeOption): WorryTimelineReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> WorryTimelineReportData(
        frequencies = List(4) { listOf(0, 1, 2, 3, 4, 1, 1) },
        summary = "목요일 저녁 시간대(18-24시)와\n일요일 밤 시간대(00-06시)에\n걱정 기록이 많았어요.",
        tip = "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요.",
    )
    DateRangeOption.LAST_2_WEEKS -> WorryTimelineReportData(
        frequencies = listOf(
            listOf(0, 1, 1, 2, 3, 1, 0),
            listOf(0, 1, 2, 3, 4, 2, 1),
            listOf(1, 2, 2, 3, 5, 2, 1),
            listOf(0, 1, 1, 2, 4, 1, 2),
        ),
        summary = "최근 2주는 금요일 오후와\n저녁 시간대에 걱정 기록이\n집중되어 있었어요.",
        tip = "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요.",
    )
    DateRangeOption.LAST_60_DAYS -> WorryTimelineReportData(
        frequencies = listOf(
            listOf(1, 2, 3, 4, 5, 2, 3),
            listOf(2, 3, 4, 5, 6, 3, 4),
            listOf(2, 3, 5, 8, 7, 3, 5),
            listOf(1, 2, 4, 6, 6, 3, 7),
        ),
        summary = "최근 60일은 목요일과 금요일\n저녁 시간대의 걱정 기록이\n가장 많았어요.",
        tip = "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요.",
    )
}
