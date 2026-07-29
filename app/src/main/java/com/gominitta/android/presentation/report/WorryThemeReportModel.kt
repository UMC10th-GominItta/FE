package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** API의 걱정 테마 응답을 화면에 전달하기 위한 계약입니다. */
data class WorryThemeReportData(
    val totalCount: Int,
    val themes: List<WorryThemeItem>,
    val feedback: String? = null,
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
    feedback?.let { return it }
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
 * API 연결 전 화면 확인용 데이터입니다.
 * 기존 화면의 테마 구성과 비율을 유지하며, 실제 API 연결 시 이 공급자만 교체합니다.
 */
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData {
    val percentages = when (range) {
        DateRangeOption.LAST_30_DAYS -> listOf(70, 40, 40, 40, 10, 10, 10)
        DateRangeOption.LAST_2_WEEKS -> listOf(55, 35, 30, 25, 15, 10, 5)
        DateRangeOption.LAST_60_DAYS -> listOf(75, 50, 45, 35, 20, 15, 10)
    }
    val themes = listOf(
        WorryTheme.CAREER,
        WorryTheme.STUDY,
        WorryTheme.STUDY,
        WorryTheme.EMPLOYMENT,
        WorryTheme.MONEY,
        WorryTheme.HEALTH,
        WorryTheme.FAMILY,
    )
    return WorryThemeReportData(
        totalCount = 100,
        themes = themes.mapIndexed { index, theme ->
            WorryThemeItem(theme, percentages[index])
        },
        feedback = when (range) {
            DateRangeOption.LAST_30_DAYS ->
                "최근에는 진로와 가족 관련된 걱정이 가장 많았어요."
            DateRangeOption.LAST_2_WEEKS ->
                "최근 2주에는 진로 관련 걱정이 가장 많았어요."
            DateRangeOption.LAST_60_DAYS ->
                "최근 60일에는 진로와 학업 고민이 꾸준히 나타났어요."
        },
    )
}
