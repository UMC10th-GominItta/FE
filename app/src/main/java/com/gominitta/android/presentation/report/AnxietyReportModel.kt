package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption
import kotlin.math.abs
import kotlin.math.roundToInt

/** 예약 시/세션 후 점수가 모두 있는 유효 매칭 데이터만 집계한 API 화면 계약입니다. */
data class AnxietyReportData(
    val matchedSetCount: Int,
    val beforeAverage: Double,
    val afterAverage: Double,
) {
    val canRender: Boolean get() = matchedSetCount >= MINIMUM_MATCHED_SET_COUNT
    val change: Double get() = afterAverage - beforeAverage
    val state: AnxietyChangeState
        get() = when {
            change < 0.0 -> AnxietyChangeState.DECREASED
            change > 0.0 -> AnxietyChangeState.INCREASED
            else -> AnxietyChangeState.MAINTAINED
        }
    val badgeText: String
        get() = when (state) {
            AnxietyChangeState.DECREASED -> "- ${abs(change).displayScore()}점 감소"
            AnxietyChangeState.INCREASED -> "+ ${change.displayScore()}점 상승"
            AnxietyChangeState.MAINTAINED -> "유지"
        }

    companion object {
        const val MINIMUM_MATCHED_SET_COUNT = 2
    }
}

enum class AnxietyChangeState { DECREASED, MAINTAINED, INCREASED }

internal fun AnxietyReportData.summaryText(): String =
    if (state == AnxietyChangeState.DECREASED) {
        "걱정을 마주하고 마음이 한결 가벼워졌어요."
    } else {
        "아직은 마음에 복잡한 생각들이 남아있네요."
    }

internal fun AnxietyReportData.tipText(): String =
    if (state == AnxietyChangeState.DECREASED) {
        "tip. 기록을 돌아보면, 걱정을 마주한 뒤 감정이 차분해지는 패턴이 보여요.\n" +
            "이 흐름을 기억하며, 앞으로도 나를 믿어보세요."
    } else {
        "tip. 불안을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요.\n" +
            "지금 나에게 가장 필요한 ‘마음 레시피’를 찾고, 실천하며 잠시 쉬어가 보세요."
    }

internal fun Double.displayScore(): String {
    val rounded = roundToInt()
    return if (abs(this - rounded) < 0.001) rounded.toString() else "%.1f".format(this)
}

internal fun anxietyDummyData(range: DateRangeOption): AnxietyReportData = when (range) {
    DateRangeOption.LAST_30_DAYS -> AnxietyReportData(6, 8.0, 4.0)
    DateRangeOption.LAST_2_WEEKS -> AnxietyReportData(3, 4.0, 8.0)
    DateRangeOption.LAST_60_DAYS -> AnxietyReportData(12, 6.0, 6.0)
}
