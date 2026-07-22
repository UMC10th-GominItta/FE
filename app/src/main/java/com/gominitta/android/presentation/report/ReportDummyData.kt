package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

internal data class WorryThemeReportData(
    val percentages: List<Int>,
    val summary: String,
)

internal data class AnxietyReportData(
    val beforeScore: Int,
    val afterScore: Int,
    val summary: String,
    val tip: String,
)

internal data class WorryTimelineReportData(
    val frequencies: List<List<Int>>,
    val summary: String,
    val tip: String,
)

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
    val scores = when (range) {
        DateRangeOption.LAST_30_DAYS -> listOf(8 to 4, 4 to 8, 6 to 6)
        DateRangeOption.LAST_2_WEEKS -> listOf(7 to 5, 5 to 7, 5 to 5)
        DateRangeOption.LAST_60_DAYS -> listOf(9 to 3, 3 to 9, 7 to 7)
    }
    val (before, after) = scores.getOrElse(cardIndex) { scores.first() }
    val summary = when {
        after < before -> "걱정을 마주하고 마음이 한결 가벼워졌어요."
        after > before -> "아직은 마음에 복잡한 생각들이 남아있네요."
        else -> "마음의 온도가 비슷하게 유지되었어요."
    }
    val tip = when {
        after < before -> "tip. 기록을 돌아보면, 걱정을 마주한 뒤 감정이 차분해지는 패턴이 보여요. 이 흐름을 기억하며, 앞으로도 나를 믿어보세요."
        after > before -> "tip. 원인을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요. 지금 나에게 필요한 마음 레시피를 찾아보세요."
        else -> "tip. 비슷한 흐름도 소중한 기록이에요. 기간별 변화를 천천히 비교해 보세요."
    }

    return AnxietyReportData(
        beforeScore = before,
        afterScore = after,
        summary = summary,
        tip = tip,
    )
}

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
        tip = "tip. 반복되는 시간대 앞뒤로 짧은 휴식 시간을 만들어 보세요.",
    )
    DateRangeOption.LAST_60_DAYS -> WorryTimelineReportData(
        frequencies = listOf(
            listOf(1, 2, 3, 4, 5, 2, 3),
            listOf(2, 3, 4, 5, 6, 3, 4),
            listOf(2, 3, 5, 8, 7, 3, 5),
            listOf(1, 2, 4, 6, 6, 3, 7),
        ),
        summary = "최근 60일은 목요일과 금요일\n저녁 시간대의 걱정 기록이\n가장 많았어요.",
        tip = "tip. 긴 기간의 흐름을 보면 나에게 필요한 휴식 패턴을 찾기 쉬워요.",
    )
}
