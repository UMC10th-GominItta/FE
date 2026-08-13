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
    val hasEnoughData: Boolean? = null,
    val reportedTotalCount: Long? = null,
) {
    val totalCount: Long get() = reportedTotalCount ?: themes.sumOf { it.count }

    /** 걱정 테마 리포트를 표시하기에 전체 걱정 기록 수가 충분한지 여부 */
    val canRender: Boolean get() = hasEnoughData ?: (totalCount >= MINIMUM_WORRY_THEME_COUNT)
}

/**
 * 걱정 테마 하나의 집계 결과입니다.
 *
 * @property theme 백엔드의 테마 코드를 화면에서 사용하는 [WorryTheme]으로 변환한 값
 * @property count 해당 테마의 걱정 기록 수
 */
data class WorryThemeItem(
    val theme: WorryTheme,
    val count: Long,
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
    OTHER("기타"),
}

/** 테마 비율을 기준으로 정한 버블의 시각적 중요도와 크기 단계입니다. */
internal enum class WorryThemeWeight { PRIMARY, NORMAL, MINOR }

/**
 * 비율순으로 정렬된 걱정 테마와 화면에서 사용할 버블 중요도를 묶은 모델입니다.
 *
 * @property item 화면에 표시할 테마와 기록 수
 * @property percentage 전체 기록에서 해당 테마가 차지하는 정수 비율
 * @property weight 테마 비율을 기준으로 계산한 버블 중요도
 */
internal data class RankedWorryTheme(
    val item: WorryThemeItem,
    val percentage: Int,
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
        if (totalCount == 0L) 0L else item.count * 100L / totalCount
    }
    val shouldPromoteFirst = percentages.values.none { it >= 30 }

    return visible.mapIndexed { index, item ->
        val percentage = percentages.getValue(item).toInt()
        val weight = when {
            percentage >= 30 -> WorryThemeWeight.PRIMARY
            shouldPromoteFirst && index == 0 -> WorryThemeWeight.PRIMARY
            percentage >= 10 -> WorryThemeWeight.NORMAL
            else -> WorryThemeWeight.MINOR
        }
        RankedWorryTheme(item, percentage, weight)
    }
}

/**
 * 중요도가 높은 큰 버블부터 카드 안에 배치합니다.
 *
 * 각 버블마다 여러 후보 좌표를 생성해 이미 배치된 버블과 겹치지 않는 첫 좌표를 선택합니다.
 * 모든 후보가 겹치면 원끼리 침범한 거리의 제곱 합이 가장 작은 좌표를 대신 선택합니다.
 * 한 번의 배치 결과에만 의존하지 않고 전체 배치를 여러 번 만든 뒤 총 겹침이 가장 작은
 * 결과를 반환합니다.
 *
 * 후보 좌표는 고정 시드 기반으로 생성하므로 같은 테마와 개수가 입력되면 항상 같은 배치를
 * 재현합니다. 따라서 랜덤한 모양을 유지하면서도 화면 재구성 때 버블 위치가 흔들리지 않습니다.
 */
internal fun layoutWorryThemeBubbles(
    themes: List<RankedWorryTheme>,
    areaWidth: Float = 319f,
    areaHeight: Float = 306f,
    gap: Float = 4f,
): List<WorryBubblePlacement> {
    if (themes.isEmpty()) return emptyList()

    // 테마 종류·개수·중요도를 모두 시드에 포함합니다. 데이터가 하나라도 달라지면 새로운
    // 후보 좌표열을 사용하고, 데이터가 같으면 동일한 좌표열을 다시 생성합니다.
    val seed = themes.fold(17) { result, ranked ->
        31 * result +
            ranked.item.theme.ordinal * 1_009 +
            ranked.item.count.hashCode() * 37 +
            ranked.weight.ordinal
    }
    var bestLayout: List<WorryBubblePlacement> = emptyList()
    var bestOverlap = Float.POSITIVE_INFINITY

    // 시드에 시도 번호를 섞어 서로 다른 전체 배치를 만들고, 그중 겹침이 가장 적은 결과를
    // 보관합니다. 완전히 겹치지 않는 결과를 찾으면 더 탐색할 필요가 없으므로 즉시 반환합니다.
    repeat(RANDOM_LAYOUT_ATTEMPTS) { attempt ->
        val random = Random(seed + attempt * 7_919)
        val placements = mutableListOf<WorryBubblePlacement>()

        // rankedThemes()가 큰 버블을 먼저 전달하므로 배치 공간이 많이 필요한 버블이 우선권을
        // 갖습니다. 이후의 작은 버블은 앞서 확정된 버블들을 피해서 좌표를 찾습니다.
        themes.forEach { ranked ->
            val size = ranked.bubbleSize()
            val maxX = max(0f, areaWidth - size)
            val maxY = max(0f, areaHeight - size)
            var bestCandidate = WorryBubblePlacement(0f, 0f, size)
            var smallestOverlap = Float.POSITIVE_INFINITY

            // 현재 버블이 카드 영역을 벗어나지 않는 범위에서 후보 좌표를 생성합니다.
            // 겹침이 0인 후보는 즉시 확정하고, 그렇지 않으면 가장 덜 겹친 후보를 기억합니다.
            for (candidateAttempt in 0 until RANDOM_CANDIDATES_PER_BUBBLE) {
                val candidate = WorryBubblePlacement(
                    x = random.nextFloat() * maxX,
                    y = random.nextFloat() * maxY,
                    size = size,
                )
                val overlap = candidate.overlapScore(placements, gap)
                if (overlap <= 0f) {
                    bestCandidate = candidate
                    smallestOverlap = 0f
                    break
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
    // 두 원 중심 사이의 거리가 반지름 합과 최소 간격보다 짧은 만큼을 침범 거리로 봅니다.
    // 큰 충돌에 더 큰 패널티를 주기 위해 침범 거리를 제곱하여 모든 기존 버블과 합산합니다.
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
private const val MINIMUM_WORRY_THEME_COUNT = 3L
