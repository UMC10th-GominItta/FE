package com.gominitta.android.presentation.report

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 선택한 기간의 세션 전후 불안 점수를 요약한 화면 모델입니다.
 *
 * @property matchedSetCount 세션 전 점수와 세션 후 점수가 모두 존재하여 평균 계산에 포함된 세션 수
 * @property beforeAverage 유효한 세션들의 세션 시작 전 불안 점수 평균
 * @property afterAverage 유효한 세션들의 세션 종료 후 불안 점수 평균
 */
data class AnxietyReportData(
    val matchedSetCount: Int,
    val beforeAverage: Double,
    val afterAverage: Double,
) {
    /** 불안 온도차 리포트를 표시하기에 유효 세션 수가 충분한지 여부 */
    val canRender: Boolean get() = matchedSetCount >= MINIMUM_MATCHED_SET_COUNT

    /** 세션 후 평균에서 세션 전 평균을 뺀 값. 음수이면 불안 점수가 감소한 상태입니다. */
    val change: Double get() = afterAverage - beforeAverage

    /** [change]의 부호를 기준으로 분류한 불안 점수 변화 상태 */
    val state: AnxietyChangeState
        get() = when {
            change < 0.0 -> AnxietyChangeState.DECREASED
            change > 0.0 -> AnxietyChangeState.INCREASED
            else -> AnxietyChangeState.MAINTAINED
        }

    /** 불안 점수의 변화량과 증감 상태를 사용자에게 보여주는 배지 문구 */
    val badgeText: String
        get() = when (state) {
            AnxietyChangeState.DECREASED -> "- ${abs(change).displayScore()}점 감소"
            AnxietyChangeState.INCREASED -> "+ ${change.displayScore()}점 상승"
            AnxietyChangeState.MAINTAINED -> "유지"
        }

    companion object {
        /** 불안 온도차 리포트를 표시하기 위해 필요한 최소 유효 세션 수 */
        const val MINIMUM_MATCHED_SET_COUNT = 2
    }
}

/** 세션 전후 불안 점수 평균의 변화 방향입니다. */
enum class AnxietyChangeState { DECREASED, MAINTAINED, INCREASED }

internal fun AnxietyReportData.summaryText(): String =
    if (state == AnxietyChangeState.DECREASED) {
        "걱정을 마주하고 마음이 한결 가벼워졌어요."
    } else {
        "아직은 마음에 복잡한 생각들이 남아있네요."
    }

internal fun AnxietyReportData.tipText(): String =
    if (state == AnxietyChangeState.DECREASED) {
        "tip. 기록을 돌아보면, 걱정을 마주한 뒤 감정이 차분해지는 패턴이 보여요. " +
            "이 흐름을 기억하며, 앞으로도 나를 믿어보세요."
    } else {
        "tip. 불안을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요. " +
            "지금 나에게 가장 필요한 ‘마음 레시피’를 찾고, 실천하며 잠시 쉬어가 보세요."
    }

internal fun Double.displayScore(): String {
    val rounded = roundToInt()
    return if (abs(this - rounded) < 0.001) rounded.toString() else "%.1f".format(this)
}
