package com.gominitta.android.presentation.report

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 선택한 기간의 세션 전후 불안 점수를 요약한 화면 모델입니다.
 *
 * @property sampleCount 세션 전후 점수 평균 계산에 포함된 세션 수
 * @property beforeScore 기간 내 예약 시 불안 점수 평균
 * @property afterScore 기간 내 마음 세션 완료 후 불안 점수 평균
 * @property gap 세션 후 평균에서 예약 시 평균을 뺀 값
 */
data class AnxietyReportData(
    val period: String,
    val beforeScore: Double,
    val afterScore: Double,
    val gap: Double,
    val sampleCount: Long,
    val feedback: String,
) {
    /** 집계된 세션이 하나라도 있으면 불안 온도차 리포트를 표시합니다. */
    val canRender: Boolean get() = sampleCount > 0

    /** [change]의 부호를 기준으로 분류한 불안 점수 변화 상태 */
    val state: AnxietyChangeState
        get() = when {
            gap < 0.0 -> AnxietyChangeState.DECREASED
            gap > 0.0 -> AnxietyChangeState.INCREASED
            else -> AnxietyChangeState.MAINTAINED
        }

    /** 불안 점수의 변화량과 증감 상태를 사용자에게 보여주는 배지 문구 */
    val badgeText: String
        get() = when (state) {
            AnxietyChangeState.DECREASED -> "- ${abs(gap).displayScore()}점 감소"
            AnxietyChangeState.INCREASED -> "+ ${gap.displayScore()}점 상승"
            AnxietyChangeState.MAINTAINED -> "유지"
        }
}

/** 세션 전후 불안 점수 평균의 변화 방향입니다. */
enum class AnxietyChangeState { DECREASED, MAINTAINED, INCREASED }

internal fun Double.displayScore(): String {
    val rounded = roundToInt()
    return if (abs(this - rounded) < 0.001) rounded.toString() else "%.1f".format(this)
}
