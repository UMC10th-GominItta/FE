package com.gominitta.android.presentation.report

import kotlin.math.hypot
import kotlin.math.max
import kotlin.random.Random

/**
 * 선택한 기간의 걱정 기록을 테마별로 요약한 화면 모델입니다.
 *
 * @property totalCount 선택한 기간에 작성된 전체 걱정 기록 수
 * @property themes 각 걱정 테마와 해당 테마의 기록 수 목록
 */
data class WorryThemeReportData(
    val period: String,
    val topCategory: WorryTheme?,
    val themes: List<WorryThemeItem>,
    val feedback: String,
) {
    val totalCount: Int get() = themes.sumOf { it.count }

    /** 걱정 테마 리포트를 표시하기에 전체 걱정 기록 수가 충분한지 여부 */
    val canRender: Boolean get() = totalCount >= MINIMUM_WORRY_COUNT

    companion object {
        /** 걱정 테마 리포트를 표시하기 위해 필요한 최소 걱정 기록 수 */
        const val MINIMUM_WORRY_COUNT = 3
    }
}

/**
 * 걱정 테마 하나의 집계 결과입니다.
 *
 * @property theme 백엔드의 테마 코드를 화면에서 사용하는 [WorryTheme]으로 변환한 값
 * @property count 해당 테마의 걱정 기록 수
 */
data class WorryThemeItem(
    val theme: WorryTheme,
    val count: Int,
)

/**
 * 걱정 기록에 사용할 수 있는 테마입니다.
 *
 * @property label 테마 코드를 사용자에게 보여줄 때 사용하는 한글 이름
 */
enum class WorryTheme(val label: String) {
    CAREER("진로"),
    EMPLOYMENT("취업"),
    STUDY("학업"),
    MONEY("돈"),
    FAMILY("가족"),
    RELATIONSHIP("관계"),
    HEALTH("건강"),
    PRESENTATION("발표"),
}

/** 테마 비율을 기준으로 정한 버블의 시각적 중요도와 크기 단계입니다. */
internal enum class WorryThemeWeight { PRIMARY, NORMAL, MINOR }

/**
 * 비율순으로 정렬된 걱정 테마와 화면에서 사용할 버블 중요도를 묶은 모델입니다.
 *
 * @property item 화면에 표시할 테마와 비율
 * @property weight 테마 비율을 기준으로 계산한 버블 중요도
 */
internal data class RankedWorryTheme(
    val item: WorryThemeItem,
    val weight: WorryThemeWeight,
)

/**
 * 걱정 테마 버블을 카드 내부에 배치하기 위한 화면 전용 좌표 모델입니다.
 *
 * @property x 버블 영역의 왼쪽을 기준으로 한 가로 위치(dp 단위로 사용)
 * @property y 버블 영역의 위쪽을 기준으로 한 세로 위치(dp 단위로 사용)
 * @property size 버블의 가로·세로 크기(dp 단위로 사용)
 */
internal data class WorryBubblePlacement(
    val x: Float,
    val y: Float,
    val size: Float,
)

/** API 배열 순서를 동률 우선순위로 유지합니다. */
internal fun WorryThemeReportData.rankedThemes(): List<RankedWorryTheme> {
    val visible = themes
        .filter { it.count > 0 }
        .sortedWith(compareByDescending<WorryThemeItem> { it.count })
    val percentages = visible.associateWith { item ->
        if (totalCount == 0) 0 else item.count * 100 / totalCount
    }
    val shouldPromoteFirst = percentages.values.none { it >= 30 }

    return visible.mapIndexed { index, item ->
        val percentage = percentages.getValue(item)
        val weight = when {
            percentage >= 30 -> WorryThemeWeight.PRIMARY
            shouldPromoteFirst && index == 0 -> WorryThemeWeight.PRIMARY
            percentage >= 10 -> WorryThemeWeight.NORMAL
            else -> WorryThemeWeight.MINOR
        }
        RankedWorryTheme(item, weight)
    }
}

/**
 * 큰 버블부터 카드 안의 좌표를 탐색합니다.
 *
 * 겹치지 않는 후보를 우선하고, 공간이 부족하면 원의 침범 거리 합이 가장 작은 후보를
 * 선택합니다. 랜덤을 사용하지 않아 같은 데이터에서는 항상 같은 결과를 반환합니다.
 */
internal fun layoutWorryThemeBubbles(
    themes: List<RankedWorryTheme>,
    areaWidth: Float = 319f,
    areaHeight: Float = 306f,
    gap: Float = 4f,
): List<WorryBubblePlacement> {
    if (themes.isEmpty()) return emptyList()
    val seed = themes.fold(17) { result, ranked ->
        31 * result +
            ranked.item.theme.ordinal * 1_009 +
            ranked.item.count * 37 +
            ranked.weight.ordinal
    }
    var bestLayout: List<WorryBubblePlacement> = emptyList()
    var bestOverlap = Float.POSITIVE_INFINITY

    repeat(RANDOM_LAYOUT_ATTEMPTS) { attempt ->
        val random = Random(seed + attempt * 7_919)
        val placements = mutableListOf<WorryBubblePlacement>()

        themes.forEach { ranked ->
            val size = ranked.bubbleSize()
            val maxX = max(0f, areaWidth - size)
            val maxY = max(0f, areaHeight - size)
            var bestCandidate = WorryBubblePlacement(0f, 0f, size)
            var smallestOverlap = Float.POSITIVE_INFINITY

            repeat(RANDOM_CANDIDATES_PER_BUBBLE) {
                val candidate = WorryBubblePlacement(
                    x = random.nextFloat() * maxX,
                    y = random.nextFloat() * maxY,
                    size = size,
                )
                val overlap = candidate.overlapScore(placements, gap)
                if (overlap <= 0f) {
                    bestCandidate = candidate
                    smallestOverlap = 0f
                    return@repeat
                }
                if (overlap < smallestOverlap) {
                    bestCandidate = candidate
                    smallestOverlap = overlap
                }
            }
            placements += bestCandidate
        }

        val totalOverlap = placements.totalOverlap(gap)
        if (totalOverlap < bestOverlap) {
            bestOverlap = totalOverlap
            bestLayout = placements
        }
        if (totalOverlap <= 0f) return bestLayout
    }
    return bestLayout
}

private fun RankedWorryTheme.bubbleSize(): Float = when (weight) {
    WorryThemeWeight.PRIMARY -> 128f
    WorryThemeWeight.NORMAL -> 92f
    WorryThemeWeight.MINOR -> 64f
}

private fun WorryBubblePlacement.overlapScore(
    placed: List<WorryBubblePlacement>,
    gap: Float,
): Float = placed.sumOf { other ->
    val radius = size / 2f
    val otherRadius = other.size / 2f
    val distance = hypot(
        (x + radius) - (other.x + otherRadius),
        (y + radius) - (other.y + otherRadius),
    )
    val intrusion = max(0f, radius + otherRadius + gap - distance)
    (intrusion * intrusion).toDouble()
}.toFloat()

private fun List<WorryBubblePlacement>.totalOverlap(gap: Float): Float =
    indices.sumOf { index ->
        this[index].overlapScore(drop(index + 1), gap).toDouble()
    }.toFloat()

private const val RANDOM_LAYOUT_ATTEMPTS = 32
private const val RANDOM_CANDIDATES_PER_BUBBLE = 1_000
