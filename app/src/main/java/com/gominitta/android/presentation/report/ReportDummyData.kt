package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** 걱정 테마 지도에 표시할 버블 비율과 분석 문구의 임시 모델입니다. */
internal data class WorryThemeReportData(
    // 진로, 학업, 학업, 취업, 돈, 건강, 가족 순서로 버블에 전달됩니다.
    val percentages: List<Int>,
    val summary: String,
)

/** 불안 온도차 카드의 예약 전후 점수와 점수 관계에 따른 피드백 모델입니다. */
internal data class AnxietyReportData(
    val beforeScore: Int,
    val afterScore: Int,
    val summary: String,
    val tip: String,
)

/**
 * 걱정 타임라인의 임시 모델입니다.
 * [frequencies]는 아침·오후·저녁·밤 4행과 월~일 7열로 구성됩니다.
 */
internal data class WorryTimelineReportData(
    val frequencies: List<List<Int>>,
    val summary: String,
    val tip: String,
)

// 기간 선택 인터랙션을 확인하기 위한 테마 지도 더미 데이터입니다.
// API 연결 시 이 함수 호출을 ViewModel이 제공하는 UI 상태로 교체합니다.
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> WorryThemeReportData(
        percentages = listOf(70, 40, 40, 40, 10, 10, 10),
        summary = "최근에는 진로와 가족 관련된 걱정이 가장 많았어요.",
    )
    DateRangeOption.LAST_2_WEEKS -> WorryThemeReportData(
        percentages = listOf(55, 35, 30, 25, 15, 10, 5),
        summary = "최근 2주에는 진로 관련 걱정이 가장 많았어요.",
    )
    DateRangeOption.LAST_60_DAYS -> WorryThemeReportData(
        percentages = listOf(75, 50, 45, 35, 20, 15, 10),
        summary = "최근 60일에는 진로와 학업 고민이 꾸준히 나타났어요.",
    )
}

internal fun anxietyDummyData(
    range: DateRangeOption,
    cardIndex: Int,
): AnxietyReportData {
    // 각 기간의 세 쌍은 감소·증가·동일 상태 Preview를 위한 값입니다.
    // 실제 ReportScreen은 첫 번째 카드 한 장만 사용하며 기간별로 세 상태를 보여줍니다.
    val scores = when (range) {
        DateRangeOption.LAST_30_DAYS -> listOf(8 to 4, 4 to 8, 6 to 6)
        DateRangeOption.LAST_2_WEEKS -> listOf(4 to 8, 6 to 6, 8 to 4)
        DateRangeOption.LAST_60_DAYS -> listOf(6 to 6, 8 to 4, 4 to 8)
    }
    val (before, after) = scores.getOrElse(cardIndex) { scores.first() }
    // 점수 관계만으로 기존 디자인 문구 중 하나를 선택합니다.
    val summary = when {
        after < before -> "걱정을 마주하고 마음이 한결 가벼워졌어요."
        after > before -> "아직은 마음에 복잡한 생각들이 남아있네요."
        else -> "아직은 마음에 복잡한 생각들이 남아있네요."
    }
    val tip = when {
        after < before -> "tip. 기록을 돌아보면, 걱정을 마주한 뒤 감정이 차분해지는 패턴이 보여요. 이 흐름을 기억하며, 앞으로도 나를 믿어보세요."
        else -> "tip. 원인을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요. 지금 나에게 가장 필요한 '마음 레시피'를 찾고, 실천하며 잠시 쉬어가 보세요."
    }

    return AnxietyReportData(
        beforeScore = before,
        afterScore = after,
        summary = summary,
        tip = tip,
    )
}

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
