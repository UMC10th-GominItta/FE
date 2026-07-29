package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption
import kotlin.math.hypot
import kotlin.math.max
import kotlin.random.Random

/** API의 걱정 테마 응답을 화면에 전달하기 위한 계약입니다. */
data class WorryThemeReportData(
    val totalCount: Int,
    val themes: List<WorryThemeItem>,
) {
    val canRender: Boolean get() = totalCount >= MINIMUM_WORRY_COUNT

    companion object {
        const val MINIMUM_WORRY_COUNT = 3
    }
}

data class WorryThemeItem(
    val theme: WorryTheme,
    val percentage: Int,
)

enum class WorryTheme(val label: String) {
    CAREER("진로"),
    EMPLOYMENT("취업"),
    STUDY("학업"),
    MONEY("돈"),
    FAMILY("가족"),
    RELATIONSHIP("관계"),
    HEALTH("건강"),
    ETC("기타"),
}

internal enum class WorryThemeWeight { PRIMARY, NORMAL, MINOR }

internal data class RankedWorryTheme(
    val item: WorryThemeItem,
    val weight: WorryThemeWeight,
)

internal data class WorryBubblePlacement(
    val x: Float,
    val y: Float,
    val size: Float,
)

/** API 배열 순서를 동률 우선순위로 유지합니다. */
internal fun WorryThemeReportData.rankedThemes(): List<RankedWorryTheme> {
    val visible = themes
        .filter { it.percentage > 0 }
        .sortedWith(compareByDescending<WorryThemeItem> { it.percentage })
    val shouldPromoteFirst = visible.none { it.percentage >= 30 }

    return visible.mapIndexed { index, item ->
        val weight = when {
            item.percentage >= 30 -> WorryThemeWeight.PRIMARY
            shouldPromoteFirst && index == 0 -> WorryThemeWeight.PRIMARY
            item.percentage >= 10 -> WorryThemeWeight.NORMAL
            else -> WorryThemeWeight.MINOR
        }
        RankedWorryTheme(item, weight)
    }
}

internal fun WorryThemeReportData.feedbackText(): String {
    val visible = themes.filter { it.percentage > 0 }
    val highest = visible.maxOfOrNull { it.percentage } ?: return ""
    val leaders = visible.filter { it.percentage == highest }
    return if (leaders.size == 1) {
        "최근에는 ${leaders.first().theme.label}과 관련된 걱정이 가장 많았어요."
    } else {
        val names = leaders.take(2).joinToString("와 ") { it.theme.label }
        "최근에는 ${names}에 대한 고민이 깊었네요."
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
            ranked.item.percentage * 37 +
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

/** API 연결 전 UT에서 기간 필터와 8개 테마 노출을 확인하기 위한 데이터입니다. */
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData {
    val percentages = when (range) {
        DateRangeOption.LAST_2_WEEKS -> listOf(29, 21, 17, 13, 8, 6, 4, 2)
        DateRangeOption.LAST_30_DAYS -> listOf(32, 20, 16, 12, 8, 5, 4, 3)
        DateRangeOption.LAST_60_DAYS -> listOf(30, 22, 16, 11, 8, 6, 4, 3)
    }
    return WorryThemeReportData(
        totalCount = 100,
        themes = WorryTheme.entries.mapIndexed { index, theme ->
            WorryThemeItem(theme, percentages[index])
        },
    )
}
